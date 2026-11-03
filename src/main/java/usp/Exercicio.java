package usp;

public class Exercicio {
    static int [] nums = {0, 0};
    static final Object lock = new Object();

    public static void incrementar(){
        synchronized(lock){
            nums[0]++;
        }
    }

    public static void teste1_UmaThreadPorIndice() throws InterruptedException{
        nums[0] = 0;
        nums[1] = 0;

        long start = System.currentTimeMillis();

        Thread t1 = new Thread(()->{
            for(int i=0;i<200_500_000;i++){
                nums[0]++;
            }
        });
        Thread t2 = new Thread(()->{
            for(int i=0;i<200_500_000;i++){
                nums[1]++;
        }});

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        long end = System.currentTimeMillis();

        System.out.printf("\nRESULDADO TESTE 1:\nNums[0]: %d\nNums[1]: %d\nTempo: %dms\n", nums[0], nums[1],  end-start);
    }

    public static void teste2_UmaUnicaThread() throws InterruptedException {
        nums[0] = 0;
        nums[1] = 0;
        long start = System.currentTimeMillis();
        Thread t1 = new Thread(()->{
            for(int i=0;i<200_500_000;i++){
                nums[0]++;
            }

            for(int i=0;i<200_500_000;i++){
                nums[1]++;
            }
        });

        t1.start();
        t1.join();

        long end = System.currentTimeMillis();

        System.out.printf("\nRESULDADO TESTE 2:\nNums[0]: %d\nNums[1]: %d\nTempo: %dms\n", nums[0], nums[1],  end-start);
    }

    public static void teste3_DuasThreadsGerais() throws InterruptedException{
        nums[0] = 0;
        nums[1] = 0;

        long start = System.currentTimeMillis();

        Thread t1 = new Thread(()->{
            for(int i=0;i<100_250_000;i++){
                synchronized (lock){
                    nums[0]++;
                    nums[1]++;
                }
            }
        });
        Thread t2 = new Thread(()->{
            for(int i=0;i<100_250_000;i++){
                synchronized (lock){
                    nums[0]++;
                    nums[1]++;
                }
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        long end = System.currentTimeMillis();

        System.out.printf("\nRESULDADO TESTE 3:\nNums[0]: %d\nNums[1]: %d\nTempo: %dms\n", nums[0], nums[1],  end-start);
    }

    public static void main(String[] args) throws InterruptedException {
        teste1_UmaThreadPorIndice();
        teste2_UmaUnicaThread();
        teste3_DuasThreadsGerais();

    }

}
