class SumThreadOther implements Runnable{

    public long partSum = 0;
    int[] mas;
    int begin, end;
    Main main;

    SumThreadOther(Main main, int[] curr_mas, int curr_begin, int curr_end) {
        mas = curr_mas;
        begin = curr_begin;
        end = curr_end;
        this.main = main;
    }

    @Override
    public void run() {
        for (int i = begin; i <= end; i++) {
            partSum = partSum + mas[i];
        }
        main.setPartSum(partSum);
    }
}

class Main {
    private long sumMas2 = 0;
    private int threadCount = 0;

    public static void main(String[] args) throws InterruptedException {
        Main main = new Main();
        main.starter();
    }

    public void starter(){
        int size = 1000000;
        int[] mas = new int[size];
        long SumMas = 0;
        for (int i = 0; i < size; i++)
            mas[i] = i;

        SumThreadOther STO = new SumThreadOther(this, mas, 0, size - 1);
        STO.run();
        for (int i = 0; i < size; i++)
            SumMas = SumMas + mas[i];
        System.out.println("Сума в однопоточному режимі:");
        System.out.println(SumMas);
        System.out.println(STO.partSum);

        int NumThread = 3;
        int[] beginMas = new int[NumThread];
        int[] endMas = new int[NumThread];
        for (int i = 0; i < NumThread; i++)
            beginMas[i] = size / NumThread * i;
        for (int i = 0; i < NumThread - 1; i++)
            endMas[i] = beginMas[i + 1] - 1;

        endMas[NumThread - 1] = size - 1;


        sumMas2 = 0;
        threadCount = 0;
        SumThreadOther[] threadsOther = new SumThreadOther[NumThread];
        Thread[] threads = new Thread[NumThread];

        for (int i = 0; i < NumThread; i++) {
            threadsOther[i] = new SumThreadOther(this, mas, beginMas[i], endMas[i]);
            threads[i] = new Thread(threadsOther[i]);
            threads[i].start();
        }

        synchronized (this) {
            while (threadCount < NumThread) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        System.out.println("Сума в багатопоточному режимі:");
        System.out.println(sumMas2);
    }

    synchronized public void setPartSum(long partSum){
        sumMas2 = sumMas2 + partSum;
        threadCount++;
        notify();
    }
}