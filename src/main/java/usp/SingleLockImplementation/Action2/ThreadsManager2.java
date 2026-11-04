package usp.SingleLockImplementation.Action2;

import usp.SingleLockImplementation.Entities2.Reader2;
import usp.SingleLockImplementation.Entities2.Writer2;

import java.util.ArrayList;
import java.util.Random;

public class ThreadsManager2 {
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
        if (interrupted) {
            Thread.currentThread().interrupt();
        }
    }
}
