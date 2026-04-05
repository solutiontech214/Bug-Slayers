package com.logger.backend.dto;

import java.util.Map;

public class LogRequest {

    private String message;
    private String level;
    private long timestamp;
    private Map<String, Object> metadata;

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    @Override
    public String toString() {
        return "LogRequest{" +
                "message='" + message + '\'' +
                ", level='" + level + '\'' +
                ", timestamp=" + timestamp +
                ", metadata=" + metadata +
                '}';
    }
}