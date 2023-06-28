import model.ChannelSimulation;
import model.ModelLogger;

import java.util.concurrent.*;

public class MainTask1 {

    public static void main(String[] args) throws InterruptedException {

        int channelsNumber = 4;
        int queueCapacity = 12;
        double serviceTime = 0.5;
        double intervalTime = 1.0;
        double simulationTime = 100.0;
        int numRequests = (int) (simulationTime / intervalTime);

        ExecutorService executor = Executors.newFixedThreadPool(channelsNumber+1);
        BlockingQueue<Long> queue = new ArrayBlockingQueue<>(queueCapacity);

        ChannelSimulation[] channels = new ChannelSimulation[channelsNumber];
        for (int i = 0; i < channelsNumber; i++) {
            channels[i] = new ChannelSimulation(queue, serviceTime, simulationTime);
        }

        ModelLogger modelLogger = new ModelLogger(queue, channels);

        executor.execute(modelLogger);

        for (int i = 0; i < channelsNumber; i++) {
            executor.execute(channels[i]);
        }

        double rejectCount = 0;
        double queueLength = 0;

        for (int i = 0; i < numRequests; i++) {

            if (queue.size() == queueCapacity) {
                rejectCount++;
            } else {
                queue.put(System.currentTimeMillis());
            }
            queueLength+= queue.size();
            modelLogger.setDroppedRequests(rejectCount);

            Thread.sleep((long) (intervalTime * 100));
        }

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        double avgQueueLength = queueLength / numRequests;
        double rejectionProbability = rejectCount / numRequests;

        System.out.println("Average queue length: " + avgQueueLength + "; Rejection: " + rejectionProbability);
    }
}
