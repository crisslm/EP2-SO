package usp.SingleLockImplementation.Entities2;

import java.util.ArrayList;
import java.util.Random;

public class Reader2 extends Thread {

    ArrayList<String> lines;
    final Object lock;

    public Reader2(ArrayList<String> lines, Object lock) {
        this.lines = lines;
        this.lock = lock;
    }

    @Override
    public void run() {
        Random rand = new Random();
        int position;
        try {
            synchronized (lock) {
                for (int i = 0; i < 100; i++) {
                    position = rand.nextInt(lines.size());
                    String local = lines.get(position);
                }
                sleep(1);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
