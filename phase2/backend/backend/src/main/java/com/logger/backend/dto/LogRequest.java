package com.logger.backend.dto;

import java.util.Map;

public class LogRequest {

    private String message;
    private String level;     // INFO, WARN, ERROR, FATAL
    private long timestamp;
    private Map<String, Object> metadata;
    private String stackTrace; // For ERROR/FATAL

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

    public String getStackTrace() { return stackTrace; }
    public void setStackTrace(String stackTrace) { this.stackTrace = stackTrace; }

    @Override
    public String toString() {
        return "LogRequest{" +
                "message='" + message + '\'' +
                ", level='" + level + '\'' +
                ", timestamp=" + timestamp +
                ", stackTrace=" + (stackTrace != null ? "[present]" : "null") +
                ", metadata=" + metadata +
                '}';
    }
}