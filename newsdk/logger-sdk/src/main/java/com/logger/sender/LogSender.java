package com.logger.sender;

import com.logger.queue.LogQueue;
import com.logger.model.LogEvent;
import com.logger.config.SDKConfig;

import java.util.List;
import java.util.concurrent.*;

import com.google.gson.Gson;

public class LogSender {

    private static ScheduledExecutorService scheduler;
    private static SDKConfig config;
    private static final Gson gson = new Gson();

    private static volatile boolean started = false;

    // 🔥 Start background worker
    public static void start(SDKConfig sdkConfig) {
        if (started) return;

        config = sdkConfig;

        scheduler = Executors.newSingleThreadScheduledExecutor();

        // Run every X seconds
        scheduler.scheduleAtFixedRate(() -> {
            try {
                processBatch();
            } catch (Exception e) {
                System.out.println("Batch processing error");
            }
        }, 0, config.getFlushIntervalSeconds(), TimeUnit.SECONDS);

        started = true;
    }

    // 🔥 Force immediate sync flush (used by CrashHandler)
    public static void flushNow() {
        if (!started) return;
        List<LogEvent> batch = LogQueue.drain(Integer.MAX_VALUE); // drain all
        if (batch.isEmpty()) return;
        try {
            String json = gson.toJson(batch);
            HttpClient.send(json, config.getApiKey(), config.getEndpoint());
        } catch (Exception e) {
            System.err.println("Failed to synchronously flush logs during crash");
        }
    }

    // 🔥 Process logs in batch
    private static void processBatch() {

        List<LogEvent> batch = LogQueue.drain(config.getBatchSize());

        if (batch.isEmpty()) return;

        try {
            // ✅ FIX: Use Gson (safe JSON)
            String json = gson.toJson(batch);

            HttpClient.send(json, config.getApiKey(), config.getEndpoint());

        } catch (Exception e) {
            System.out.println("Failed to send batch logs");
        }
    }

    // 🔹 Optional: shutdown
    public static void shutdown() {
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }
}