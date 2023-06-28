package model;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class ChannelSimulation implements Runnable {

    private final BlockingQueue<Long> queue;
    private final double serviceTime;
    private final double simTime;
    private boolean isRunning = true;
    private Long lastElement = null;

    public ChannelSimulation(BlockingQueue<Long> queue, double serviceTime, double simTime) {
        this.queue = queue;
        this.serviceTime = serviceTime;
        this.simTime = simTime;
    }

    public Long getLastElement() {
        return this.lastElement;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    @Override
    public void run() {

        double lastEventTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - lastEventTime < simTime || !queue.isEmpty()) {

            try {
                this.lastElement = queue.poll(10L, TimeUnit.MICROSECONDS);
                if (this.lastElement != null) {
                    lastEventTime = System.currentTimeMillis();
                }
                Thread.sleep((long) (serviceTime * 1000));
            } catch (InterruptedException e) {  }
        }

        this.isRunning = false;
    }
}