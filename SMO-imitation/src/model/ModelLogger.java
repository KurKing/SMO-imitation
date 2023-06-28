package model;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;

public class ModelLogger implements Runnable {

    private final int id;
    private final BlockingQueue<Long> queue;
    private ChannelSimulation[] channels;
    private double droppedRequests;

    public ModelLogger(int id, BlockingQueue<Long> queue,  ChannelSimulation[] channels) {
        this.id = id;
        this.channels = channels;
        this.queue = queue;
    }

    public ModelLogger(BlockingQueue<Long> queue,  ChannelSimulation[] channels) {
        this.id = -2;
        this.channels = channels;
        this.queue = queue;
    }

    public void setDroppedRequests(double numDroppedRequests) {
        this.droppedRequests = numDroppedRequests;
    }

    private boolean isChannelsActive() {
        return Arrays.stream(channels).anyMatch(ChannelSimulation::isRunning);
    }

    @Override
    public void run() {

        while (isChannelsActive()) {
            try {
                System.out.println(this);
                Thread.sleep(1000);
            } catch (InterruptedException e) {  }
        }
    }

    @Override
    public String toString() {

        String str = "";
        if (id >= 0) { str += id + ": "; }

        str += "Queue Length: " + queue.size() + "; Dropped requests: " + droppedRequests;
        for (int i = 0; i < channels.length; i++) {
            str += "\n\t Last for " + (i + 1) + ": " + channels[i].getLastElement();
        }

        return str;
    }
}
