package usp.SingleLockImplementation.Entities2;

import java.util.ArrayList;
import java.util.Random;

public class Writer2 extends Thread {

    ArrayList<String> lines;
    final Object lock;

    public Writer2(ArrayList<String> lines, Object lock) {
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
                    lines.set(position, "MODIFICADO");
                }
                sleep(1);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                "Worker interrompido; a rodada não pode ser contabilizada", e
            );
        }
    }
}
