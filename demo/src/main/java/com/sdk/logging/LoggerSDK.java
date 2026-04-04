package com.sdk.logging;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;

public class LoggerSDK {

    private final String serverUrl;
    private final String appName;
    private final String environment;
    private final String module;
    private final String apiKey;

    private final List<String> logQueue = Collections.synchronizedList(new ArrayList<>());
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final int batchSize;
    private final int maxRetries;

    public LoggerSDK(String serverUrl, String appName, String environment,
                     String module, String apiKey) {
        this(serverUrl, appName, environment, module, apiKey, 5, 3);
    }

    public LoggerSDK(String serverUrl, String appName, String environment,
                     String module, String apiKey, int batchSize, int maxRetries) {

        this.serverUrl = serverUrl;
        this.appName = appName;
        this.environment = environment;
        this.module = module;
        this.apiKey = apiKey;
        this.batchSize = batchSize;
        this.maxRetries = maxRetries;

        // Schedule batch flush every 5 sec
        scheduler.scheduleAtFixedRate(this::flushLogs, 5, 5, TimeUnit.SECONDS);

        // Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(this::flushLogs));

        // Crash handler (VERY IMPORTANT)
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            fatal("Unhandled exception: " + throwable.getMessage(), throwable);
            flushLogs();
        });
    }

    // ---------------- LOG METHODS ----------------

    public void info(String message) {
        addLog(LogLevel.INFO, message, null);
    }

    public void warn(String message) {
        addLog(LogLevel.WARN, message, null);
    }

    public void debug(String message) {
        addLog(LogLevel.DEBUG, message, null);
    }

    public void error(String message, Throwable t) {
        addLog(LogLevel.ERROR, message, t);
    }

    public void fatal(String message, Throwable t) {
        addLog(LogLevel.FATAL, message, t);
    }

    // ---------------- CORE LOGIC ----------------

    private void addLog(LogLevel level, String message, Throwable t) {

        String stackTrace = null;

        if (t != null) {
            StringBuilder sb = new StringBuilder();
            for (StackTraceElement el : t.getStackTrace()) {
                sb.append(el.toString()).append("\\n");
            }
            stackTrace = sb.toString();
        }

        LogRequest log = new LogRequest(
                level.name(),
                message,
                appName,
                environment,
                module,
                stackTrace
        );

        logQueue.add(log.toJson());

        if (logQueue.size() >= batchSize) {
            flushLogs();
        }
    }

    private void flushLogs() {
        if (logQueue.isEmpty()) return;

        List<String> batch;
        synchronized (logQueue) {
            batch = new ArrayList<>(logQueue);
            logQueue.clear();
        }

        sendWithRetry(batch);
    }

    private void sendWithRetry(List<String> batch) {
        int attempt = 0;

        while (attempt < maxRetries) {
            try {
                sendBatch(batch);
                return;
            } catch (Exception e) {
                attempt++;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {}
            }
        }

        System.err.println("Batch failed after retries");
    }

    private void sendBatch(List<String> batch) throws Exception {
        URL url = new URL(serverUrl + "/logs/batch");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("x-api-key", apiKey);
        conn.setConnectTimeout(3000);
        conn.setReadTimeout(3000);
        conn.setDoOutput(true);

        String jsonArray = "[" + String.join(",", batch) + "]";

        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonArray.getBytes());
            os.flush();
        }

        int code = conn.getResponseCode();

        if (code != 200 && code != 201) {
            throw new RuntimeException("HTTP Error: " + code);
        }
    }
}