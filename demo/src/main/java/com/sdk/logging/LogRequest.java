package com.sdk.logging;

import java.time.LocalDateTime;

public class LogRequest {

    private final String level;
    private final String message;
    private final String projectId;
    private final String moduleId;
    private final String submoduleId;
    private final String environment;
    private final String stackTrace;
    private final LocalDateTime timestamp;

    public LogRequest(String level,
                      String message,
                      String projectId,
                      String moduleId,
                      String submoduleId,
                      String environment,
                      String stackTrace) {

        this.level = level;
        this.message = message;
        this.projectId = projectId;
        this.moduleId = moduleId;
        this.submoduleId = submoduleId;
        this.environment = environment;
        this.stackTrace = stackTrace;
        this.timestamp = LocalDateTime.now();
    }

    // 🔥 Safe JSON escape (VERY IMPORTANT)
    private String escape(String str) {
        if (str == null) return "";
        return str
                .replace("\"", "'")
                .replace("\n", " ")
                .replace("\r", " ");
    }

    public String toJson() {
        return String.format(
                "{\"level\":\"%s\",\"message\":\"%s\",\"projectId\":\"%s\",\"moduleId\":\"%s\",\"submoduleId\":\"%s\",\"environment\":\"%s\",\"stackTrace\":\"%s\",\"timestamp\":\"%s\"}",
                escape(level),
                escape(message),
                escape(projectId),
                escape(moduleId),
                escape(submoduleId),
                escape(environment),
                escape(stackTrace),
                timestamp.toString()
        );
    }
}