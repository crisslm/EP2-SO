package usp;

import java.util.*;
import java.util.concurrent.atomic.*;
import usp.ReadWriteImplementation.Action.*;
import usp.ReadWriteImplementation.Entities.*;
import usp.SingleLockImplementation.Action2.*;
import usp.SingleLockImplementation.Entities2.*;
import usp.ReadWriteImplementation.Implementation.*;
import usp.SingleLockImplementation.Implementation.*;

public class BenchmarkRegressionChecks {
    static int failed;
    static void check(boolean ok, String message) { if (!ok) throw new AssertionError(message); }
    static void test(String name, Runnable action) {
        try { action.run(); System.out.println("PASS " + name); }
        catch (Throwable e) { failed++; System.out.println("FAIL " + name + ": " + e); }
    }
    static ArrayList<String> data() { return new ArrayList<>(Collections.nCopies(100, "word")); }
    static void join(Thread t) {
        try { t.join(5000); } catch (InterruptedException e) { throw new AssertionError(e); }
        check(!t.isAlive(), "thread did not finish");
    }
    static void awaitState(Thread t, Thread.State state) {
        long deadline = System.nanoTime() + 2_000_000_000L;
        while (t.isAlive() && t.getState() != state && System.nanoTime() < deadline) Thread.onSpinWait();
        check(t.getState() == state, "expected state " + state);
    }
    static class Counting extends ArrayList<String> {
        final AtomicInteger accesses = new AtomicInteger();
        Counting() { super(Collections.nCopies(100, "word")); }
        public String get(int i) { accesses.incrementAndGet(); return super.get(i); }
        public String set(int i, String value) { accesses.incrementAndGet(); return super.set(i, value); }
    }
    static class Reject extends ReadWriteController {
        int releases;
        public synchronized void startRead() throws InterruptedException { throw new InterruptedException(); }
        public synchronized void startWrite() throws InterruptedException { throw new InterruptedException(); }
        public synchronized void endRead() { releases++; }
        public synchronized void endWrite() { releases++; }
    }
    static void interruptedWorker(int type) {
        // Own interrupt before start: deterministic interruption at sleep(1).
        Object lock = new Object(); ReadWriteController c = new ReadWriteController();
        Thread worker = switch(type) {
            case 0 -> new Reader(data(), c);
            case 1 -> new Writer(data(), c);
            case 2 -> new Reader2(data(), lock);
            default -> new Writer2(data(), lock);
        };
        AtomicReference<Throwable> error = new AtomicReference<>(); AtomicBoolean flag = new AtomicBoolean();
        worker.setUncaughtExceptionHandler((t,e)->{error.set(e);flag.set(t.isInterrupted());});
        // Wrapping run sets interrupt in the worker itself, avoiding semantics of interrupting NEW threads.
        Thread runner = new Thread(()->{Thread.currentThread().interrupt();worker.run();});
        runner.setUncaughtExceptionHandler(worker.getUncaughtExceptionHandler()); runner.start(); join(runner);
        check(error.get() instanceof IllegalStateException && error.get().getCause() instanceof InterruptedException && flag.get(), "interruption not propagated with restored flag");
        Thread next = type < 2 ? new Writer(data(), c) : new Writer2(data(), lock);
        AtomicReference<Throwable> nextError = new AtomicReference<>(); next.setUncaughtExceptionHandler((t,e)->nextError.set(e));
        next.start(); join(next); check(nextError.get() == null, "lock was not released");
    }
    static void interruptedManager(boolean single) {
        Thread[] workers = new Thread[100]; Object lock = new Object(); ReadWriteController c = new ReadWriteController();
        ThreadsManager m = new ThreadsManager(data(), c, workers, 100, 0);
        ThreadsManager2 m2 = new ThreadsManager2(data(), lock, workers, 100, 0);
        if(single) m2.create(); else m.create();
        AtomicReference<Throwable> error = new AtomicReference<>(); AtomicBoolean flag = new AtomicBoolean();
        Thread manager = new Thread(()->{try {if(single)m2.start();else m.start();}catch(Throwable e){error.set(e);flag.set(Thread.currentThread().isInterrupted());}});
        Runnable interrupt = ()->{manager.start();awaitState(manager,Thread.State.WAITING);manager.interrupt();
            try {Thread.sleep(10);} catch(InterruptedException e){throw new AssertionError(e);}
            check(manager.isAlive(),"returned with blocked workers");};
        if(single) { synchronized(lock) { interrupt.run(); } }
        else { try { c.startWrite(); } catch(InterruptedException e){throw new AssertionError(e);} try {interrupt.run();} finally {c.endWrite();} }
        join(manager);for(Thread t:workers)check(!t.isAlive(),"worker left alive");
        check(error.get() instanceof IllegalStateException && flag.get(),"interrupted benchmark was accepted");
    }
    static void interruptedAfterWorkersFinish(boolean single) {
        AtomicReference<Throwable> error = new AtomicReference<>();
        AtomicBoolean flag = new AtomicBoolean();
        Thread manager = new Thread(() -> {
            Thread[] workers = new Thread[100];
            for (int i = 0; i < workers.length; i++) {
                boolean last = i == workers.length - 1;
                workers[i] = new Thread(() -> {}) {
                    @Override
                    public synchronized void start() {
                        super.start();
                        // Cada worker realmente termina antes de start() retornar.
                        try {
                            join();
                        } catch (InterruptedException e) {
                            throw new AssertionError(e);
                        }
                        if (last) Thread.currentThread().interrupt();
                    }
                };
            }
            try {
                if (single) new ThreadsManager2(data(), new Object(), workers, 100, 0).start();
                else new ThreadsManager(data(), new ReadWriteController(), workers, 100, 0).start();
            } catch (Throwable e) {
                error.set(e);
                flag.set(Thread.currentThread().isInterrupted());
            }
            for (Thread t : workers) check(t.getState() == Thread.State.TERMINATED, "worker not terminated");
        });
        AtomicReference<Throwable> unexpected = new AtomicReference<>();
        manager.setUncaughtExceptionHandler((t, e) -> unexpected.set(e));
        manager.start();
        join(manager);
        check(unexpected.get() == null, "unexpected worker setup failure");
        check(error.get() instanceof IllegalStateException && flag.get(),
            "interrupt flag accepted when joins return immediately");
    }
    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((t,e)->{});
        for(boolean single:new boolean[]{false,true}) {
            test("interrupt after workers finish single="+single,()->interruptedAfterWorkersFinish(single));
            test("worker failure rejects benchmark single="+single,()->{try{if(single)new Single_Lock_implementation().SLImplementation(50,50,new ArrayList<>());else new Readers_and_Writers_Implementation().RWImplementation(50,50,new ArrayList<>());throw new AssertionError("accepted failed benchmark");}catch(IllegalStateException e){check(e.getCause() instanceof IllegalArgumentException,"missing worker cause");}});
            test("manager interrupt waits and rejects single="+single,()->interruptedManager(single));
            for(int readers:new int[]{0,50,100})test("normal 100-thread batch single="+single+" readers="+readers,()->{Counting d=new Counting();Thread[]ts=new Thread[100];if(single){ThreadsManager2 m=new ThreadsManager2(d,new Object(),ts,100-readers,readers);m.create();m.start();}else{ThreadsManager m=new ThreadsManager(d,new ReadWriteController(),ts,100-readers,readers);m.create();m.start();}check(d.accesses.get()==10_000,"wrong accesses");for(Thread t:ts)check(!t.isAlive(),"worker alive");});
        }
        for(int i=0;i<4;i++){int type=i;test("worker interruption type="+type,()->interruptedWorker(type));}
        test("failed RW acquisition never releases",()->{Reject c=new Reject();for(Thread t:new Thread[]{new Reader(data(),c),new Writer(data(),c)}){AtomicReference<Throwable> error=new AtomicReference<>();t.setUncaughtExceptionHandler((worker,e)->error.set(e));t.start();join(t);check(error.get() instanceof IllegalStateException,"missing failure");}check(c.releases==0,"unacquired access released");});
        System.out.println("FAILURES="+failed); System.exit(failed==0?0:1);
    }
}
