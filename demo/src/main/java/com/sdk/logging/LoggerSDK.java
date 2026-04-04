package com.sdk.logging;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;

public class LoggerSDK {

    private final String serverUrl;
    private final String appName;

    // Batch storage
    private final List<String> logQueue = Collections.synchronizedList(new ArrayList<>());

    // Scheduler (runs every few seconds)
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // Config
    private final int batchSize = 5;
    private final int maxRetries = 3;

    public LoggerSDK(String serverUrl, String appName) {
        this.serverUrl = serverUrl;
        this.appName = appName;

        // Start batch sender every 5 seconds
        scheduler.scheduleAtFixedRate(this::flushLogs, 5, 5, TimeUnit.SECONDS);
    }

    // Public methods
    public void info(String message) {
        addLog("INFO", message);
    }

    public void error(String message) {
        addLog("ERROR", message);
    }

    public void warn(String message) {
        addLog("WARN", message);
    }

    public void debug(String message) {
        addLog("DEBUG", message);
    }

    // Add log to queue
    private void addLog(String level, String message) {
        String json = String.format(
                "{\"level\":\"%s\",\"message\":\"%s\",\"appName\":\"%s\"}",
                level, message, appName
        );

        logQueue.add(json);

        // If batch size reached → send immediately
        if (logQueue.size() >= batchSize) {
            flushLogs();
        }
    }

    // Send batch
    private void flushLogs() {
        if (logQueue.isEmpty()) return;

        List<String> batch;
        synchronized (logQueue) {
            batch = new ArrayList<>(logQueue);
            logQueue.clear();
        }

        sendWithRetry(batch);
    }

    // Retry logic
    private void sendWithRetry(List<String> batch) {
        int attempt = 0;

        while (attempt < maxRetries) {
            try {
                sendBatch(batch);
                return;
            } catch (Exception e) {
                attempt++;
                System.out.println("Batch retry " + attempt);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {}
            }
        }

        System.out.println("Batch failed after retries");
    }

    // Send HTTP batch
    private void sendBatch(List<String> batch) throws Exception {
        URL url = new URL(serverUrl + "/logs/batch");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        String jsonArray = "[" + String.join(",", batch) + "]";

        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonArray.getBytes());
            os.flush();
        }

        int responseCode = conn.getResponseCode();

        if (responseCode != 200 && responseCode != 201) {
            throw new RuntimeException("HTTP Error: " + responseCode);
        }
    }
}