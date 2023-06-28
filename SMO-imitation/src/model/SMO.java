package model;

import java.util.concurrent.*;

public class SMO implements Runnable {

    private final int numChannels;
    private final int queueCapacity;
    private final double serviceTime;
    private final double intervalTime;
    private final double simTime;
    private final ExecutorService executor;
    private final int id;

    BlockingQueue<Long> queue;
    ChannelSimulation[] channels;
    public double avgQueueLength;
    public double rejectionProbability;

    public SMO(int numChannels, int queueCapacity, double serviceTime,
               double intervalTime, double simTime, int id) {

        this.numChannels = numChannels;
        this.queueCapacity = queueCapacity;
        this.serviceTime = serviceTime;
        this.intervalTime = intervalTime;
        this.simTime = simTime;
        this.id = id;

        executor = Executors.newFixedThreadPool(numChannels + 1);
        queue = new ArrayBlockingQueue<>(queueCapacity);
        channels = new ChannelSimulation[numChannels];

        for (int i = 0; i < numChannels; i++) {
            channels[i] = new ChannelSimulation(queue, serviceTime, simTime);
        }
    }

    @Override
    public void run() {

        int numRequests = (int) (simTime / intervalTime);

        ModelLogger modelLogger = new ModelLogger(id, queue, channels);

        executor.execute(modelLogger);

        for (int i = 0; i < numChannels; i++) {
            executor.execute(channels[i]);
        }

        double rejectCount = 0;
        double queueLength = 0;

        for (int i = 0; i < numRequests; i++) {
            try {
                if (queue.size() == queueCapacity) {
                    rejectCount++;
                } else {
                    queue.put(System.currentTimeMillis());
                }
                queueLength+= queue.size();
                modelLogger.setDroppedRequests(rejectCount);
                Thread.sleep((long) (intervalTime * 100));
            } catch (InterruptedException e) { }
        }

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) { }

        avgQueueLength = queueLength / numRequests;
        rejectionProbability = rejectCount / numRequests;
    }
}
