package mn.von.gestalt.utility.thread;

import java.util.concurrent.LinkedBlockingQueue;

public class GestaltThreadPool {

    //Thread pool size
    private final int poolSize;

    //Internally pool is an array
    private final WorkerThread[] workers;

    // FIFO ordering
    private final LinkedBlockingQueue<Runnable> queue;

    public GestaltThreadPool() {
        this.poolSize = getCpuCores();
        queue = new LinkedBlockingQueue<Runnable>();
        workers = new WorkerThread[poolSize];

        for (int i = 0; i < poolSize; i++) {
            workers[i] = new WorkerThread();
            workers[i].start();
        }
    }

    public GestaltThreadPool(int poolSize) {
        this.poolSize = poolSize;
        queue = new LinkedBlockingQueue<Runnable>();
        workers = new WorkerThread[poolSize];

        for (int i = 0; i < poolSize; i++) {
            workers[i] = new WorkerThread();
            workers[i].start();
        }
    }

    private int getCpuCores() {
        int cpuCores = Runtime.getRuntime().availableProcessors();
        if(cpuCores < 2) return 1;
        return cpuCores - 2;
    }

    public void execute(Runnable task) {
        synchronized (queue) {
            queue.add(task);
            queue.notify();
        }
    }

    public void shutdown() {
        for (int i = 0; i < poolSize; i++) {
            workers[i] = null;
        }
    }

    private class WorkerThread extends Thread {
        public void run() {
            Runnable task;

            while (true) {
                synchronized (queue) {
                    while (queue.isEmpty()) {
                        try {
                            queue.wait();
                        } catch (InterruptedException e) {
                            System.out.println("An error occurred while queue is waiting: " + e.getMessage());
                        }
                    }
                    task = (Runnable) queue.poll();
                }

                try {
                    task.run();
                } catch (RuntimeException e) {
                    System.out.println("Thread pool is interrupted due to an issue: " + e.getMessage());
                }
            }
        }
    }

}
