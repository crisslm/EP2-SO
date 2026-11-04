package usp.ReadWriteImplementation.Entities;

import usp.ReadWriteImplementation.Action.ReadWriteController;

import java.util.ArrayList;
import java.util.Random;

public class Writer extends Thread{

    ArrayList<String> lines;
    ReadWriteController readWriteController;
    public Writer(ArrayList<String> lines, ReadWriteController readWriteController){
        this.lines = lines;
        this.readWriteController = readWriteController;
    }

    @Override
    public void run(){
        Random rand = new Random();
        int position;
        boolean accessAcquired = false;

        try{
            readWriteController.startWrite();
            accessAcquired = true;
            for(int i=0;i<100;i++){
                position = rand.nextInt(lines.size());
                lines.set(position, "MODIFICADO");
            }
            sleep(1);
        } catch(InterruptedException e){
            Thread.currentThread().interrupt();
        } finally{
            if (accessAcquired) {
                readWriteController.endWrite();
            }
        }
    }
}
