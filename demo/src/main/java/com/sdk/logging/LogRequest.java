package com.sdk.logging;

import java.time.LocalDateTime;

public class LogRequest {

    private String level;
    private String message;
    private String appName;
    private String environment;
    private String module;
    private String stackTrace;
    private LocalDateTime timestamp;

    public LogRequest(String level, String message, String appName,
                      String environment, String module, String stackTrace) {
        this.level = level;
        this.message = message;
        this.appName = appName;
        this.environment = environment;
        this.module = module;
        this.stackTrace = stackTrace;
        this.timestamp = LocalDateTime.now();
    }

    public String toJson() {
        return String.format(
                "{\"level\":\"%s\",\"message\":\"%s\",\"appName\":\"%s\",\"environment\":\"%s\",\"module\":\"%s\",\"stackTrace\":\"%s\",\"timestamp\":\"%s\"}",
                level, message, appName, environment, module,
                stackTrace == null ? "" : stackTrace.replace("\"", "'"),
                timestamp.toString()
        );
    }
}