package usp.SingleLockImplementation.Implementation;

import usp.SingleLockImplementation.Action2.ThreadsManager2;

import java.util.ArrayList;

public class Single_Lock_implementation {

    public int SLImplementation(int writers, int readers, ArrayList<String> lines){
        Thread [] threads =  new Thread[100];
        Object lock = new Object();

        ThreadsManager2 threadsManager2 = new ThreadsManager2(lines, lock, threads, writers, readers);
        threadsManager2.create();

        long start = System.currentTimeMillis();
        threadsManager2.start();
        long end  = System.currentTimeMillis();

        return (int) (end - start);
    }
}
