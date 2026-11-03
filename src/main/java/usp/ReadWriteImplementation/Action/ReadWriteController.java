package usp.ReadWriteImplementation.Action;

public class ReadWriteController {
    private int readersCount = 0;
    private boolean writing = false;

    public synchronized void startRead() throws InterruptedException{
        while(writing){
            wait();
        }
        readersCount++;
    }

    public synchronized void endRead(){
        readersCount--;
        if(readersCount == 0){
            notifyAll();
        }
    }

    public synchronized void startWrite() throws InterruptedException{
        while(writing || readersCount > 0){
            wait();
        }
        writing = true;
    }

    public synchronized void endWrite(){
        writing = false;
        notifyAll();
    }
}
