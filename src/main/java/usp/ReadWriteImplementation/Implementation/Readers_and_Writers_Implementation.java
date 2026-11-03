package usp.ReadWriteImplementation.Implementation;

import usp.ReadWriteImplementation.Action.PopulateArray;
import usp.ReadWriteImplementation.Action.ReadWriteController;
import usp.ReadWriteImplementation.Action.ThreadsManager;

import java.util.ArrayList;

public class Readers_and_Writers_Implementation {

    public int RWImplementation(int writers, int readers, ArrayList<String> lines){
        Thread [] threads =  new Thread[100];
        ReadWriteController readWriteController = new ReadWriteController();

        ThreadsManager threadsManager = new ThreadsManager(lines, readWriteController, threads, writers, readers);
        threadsManager.create();

        long start = System.currentTimeMillis();
        threadsManager.start();
        long end  = System.currentTimeMillis();

        return (int) (end - start);
    }
}
