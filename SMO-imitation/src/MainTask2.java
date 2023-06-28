import model.SMO;

import java.util.Arrays;
import java.util.concurrent.*;

public class MainTask2 {

    public static void main(String[] args) throws InterruptedException {

        int numChannels = 4;
        int queueCapacity = 10;
        double serviceTime = 0.5;
        double intervalTime = 0.5;
        double simTime = 100.0;
        int smoCount = 2;

        ExecutorService executor = Executors.newFixedThreadPool(smoCount);

        SMO[] smos = new SMO[smoCount];

        for (int i = 0; i < smoCount; i++) {

            SMO smo = new SMO(numChannels, queueCapacity, serviceTime, intervalTime, simTime, i);
            smos[i] = smo;

            executor.execute(smo);
        }

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {  }

        System.out.println("\nAverage queue length: " + averageQueueLengthFrom(smos) + "; Rejection: " + rejectionProbabilityFrom(smos));
    }

    private static double averageQueueLengthFrom(SMO[] smos) {

        double avgQueueLength = Arrays.stream(smos)
                .map(smo -> smo.avgQueueLength)
                .reduce(.0,Double::sum);
        return avgQueueLength / smos.length;
    }

    private static double rejectionProbabilityFrom(SMO[] smos) {

        double rejectionProbability = Arrays.stream(smos)
                .map(smo -> smo.rejectionProbability)
                .reduce(.0,Double::sum);
        return rejectionProbability / smos.length;
    }
}