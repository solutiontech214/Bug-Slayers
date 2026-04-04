package com.logger.queue;

import com.logger.model.LogEvent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.List;
import java.util.ArrayList;

public class LogQueue {

    // 🔥 Thread-safe queue
    private static final BlockingQueue<LogEvent> queue = new LinkedBlockingQueue<>();

    // 🔹 Add log (non-blocking)
    public static void add(LogEvent logEvent) {
        if (logEvent == null) return;

        queue.offer(logEvent); // does not block
    }

    // 🔹 Get single log (used if needed)
    public static LogEvent take() throws InterruptedException {
        return queue.take(); // blocks until available
    }

    // 🔥 Drain logs for batching
    public static List<LogEvent> drain(int maxBatchSize) {
        List<LogEvent> batch = new ArrayList<>();

        // drain up to maxBatchSize logs
        queue.drainTo(batch, maxBatchSize);

        return batch;
    }

    // 🔹 Check size (for debugging / monitoring)
    public static int size() {
        return queue.size();
    }

    // 🔹 Check if empty
    public static boolean isEmpty() {
        return queue.isEmpty();
    }
}