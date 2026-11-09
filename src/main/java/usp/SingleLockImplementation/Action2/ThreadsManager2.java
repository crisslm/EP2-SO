package usp.SingleLockImplementation.Action2;

import usp.SingleLockImplementation.Entities2.Reader2;
import usp.SingleLockImplementation.Entities2.Writer2;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class ThreadsManager2 {
    private final AtomicReference<Throwable> workerFailure = new AtomicReference<>();

    Thread [] threads;
    ArrayList<String> lines;
    Object lock;
    int writers;
    int readers;
    public ThreadsManager2(ArrayList<String> lines, Object lock, Thread[] threads, int writers, int readers){
        this.lines = lines;
        this.lock = lock;
        this.threads = threads;
        this.writers = writers;
        this.readers = readers;
    }

    public void create(){
        Random random = new Random();
        int aux1 = writers;
        while(aux1 > 0){
            int i =  random.nextInt(100);
            if(threads[i] == null){
                threads[i] = new Writer2(lines, lock);
                aux1--;
            }
        }

        int aux2 = readers;
        while(aux2 > 0){
            int i =  random.nextInt(100);
            if(threads[i] == null){
                threads[i] = new Reader2(lines, lock);
                aux2--;
            }
        }
        // Configuração fora do intervalo medido, antes de iniciar os workers.
        for (Thread thread : threads) {
            thread.setUncaughtExceptionHandler((worker, error) -> {
                workerFailure.compareAndSet(null, error);
            });
        }
    }

    public void start(){
        for(Thread thread : threads){
            thread.start();
        }
        boolean interrupted = false;
        for(Thread thread : threads){
            while (true) {
                try {
                    thread.join();
                    break;
                } catch (InterruptedException e) {
                    interrupted = true;
                }
            }
        }
        // join() pode retornar sem lançar se a thread já terminou.
        interrupted = interrupted || Thread.currentThread().isInterrupted();
        if (interrupted) {
            Thread.currentThread().interrupt();
        }

        Throwable failure = workerFailure.get();
        if (failure != null) {
            throw new IllegalStateException(
                "Rodada inválida: uma das threads falhou", failure
            );
        }

        if (interrupted) {
            throw new IllegalStateException(
                "Experimento interrompido enquanto aguardava as threads"
            );
        }
    }
}
