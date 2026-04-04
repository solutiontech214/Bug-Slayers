package com.sdk.logging;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;

public class LoggerSDK {

    private final String serverUrl;
    private final String apiKey;

    // Auto-loaded config
    private String projectId;
    private String moduleId;
    private String submoduleId;
    private String environment;

    private final List<String> logQueue = Collections.synchronizedList(new ArrayList<>());
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final int batchSize;
    private final int maxRetries;

    // ---------------- CONSTRUCTOR ----------------

    public LoggerSDK(String serverUrl, String apiKey) {
        this(serverUrl, apiKey, 5, 3);
    }

    public LoggerSDK(String serverUrl, String apiKey, int batchSize, int maxRetries) {
        this.serverUrl = serverUrl;
        this.apiKey = apiKey;
        this.batchSize = batchSize;
        this.maxRetries = maxRetries;

        // 🔥 Load config from backend
        loadConfig();

        // Start batch scheduler
        scheduler.scheduleAtFixedRate(this::flushLogs, 5, 5, TimeUnit.SECONDS);

        // Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(this::flushLogs));

        // Crash handler
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            System.err.println("FATAL ERROR: " + throwable.getMessage());
            addLog(LogLevel.FATAL, "Unhandled exception", throwable);
            flushLogs();
        });
    }

    // ---------------- CONFIG FETCH ----------------

    private void loadConfig() {
        try {
            URL url = new URL(serverUrl + "/config");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("x-api-key", apiKey);
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed to fetch config");
            }

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream())
            );

            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            parseConfig(response.toString());

        } catch (Exception e) {
            throw new RuntimeException("SDK config load failed: " + e.getMessage());
        }
    }

    // Simple JSON parser (lightweight)
    private void parseConfig(String json) {
        this.projectId = extract(json, "projectId");
        this.moduleId = extract(json, "moduleId");
        this.submoduleId = extract(json, "submoduleId");
        this.environment = extract(json, "environment");
    }

    private String extract(String json, String key) {
        String pattern = "\"" + key + "\":\"";
        int start = json.indexOf(pattern);

        if (start == -1) return "";

        start += pattern.length();
        int end = json.indexOf("\"", start);

        return json.substring(start, end);
    }

    // ---------------- PUBLIC LOG METHODS ----------------

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
                sb.append(el.toString()).append(" | ");
            }
            stackTrace = sb.toString();
        }

        LogRequest log = new LogRequest(
                level.name(),
                message,
                projectId,
                moduleId,
                submoduleId,
                environment,
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
        }

        int code = conn.getResponseCode();

        if (code != 200 && code != 201) {
            throw new RuntimeException("HTTP Error: " + code);
        }
    }
}