package usp.ReadWriteImplementation.Entities;

import usp.ReadWriteImplementation.Action.ReadWriteController;

import java.util.ArrayList;
import java.util.Random;

public class Reader extends Thread{

    ArrayList<String> lines;
    ReadWriteController readWriteController;
    public Reader(ArrayList<String> lines, ReadWriteController readWriteController){
        this.lines = lines;
        this.readWriteController = readWriteController;
    }

    @Override
    public void run(){
        Random rand = new Random();
        int position;
        boolean accessAcquired = false;
        try{
            readWriteController.startRead();
            accessAcquired = true;
            for(int i=0;i<100;i++){
                position = rand.nextInt(lines.size());
                String local = lines.get(position);
            }
            sleep(1);
        } catch(InterruptedException e){
            Thread.currentThread().interrupt();
        } finally {
            if (accessAcquired) {
                readWriteController.endRead();
            }
        }
    }
}
