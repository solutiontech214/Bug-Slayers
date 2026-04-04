package com.sdk.logging;

public class LogRequest {

    private String level;
    private String message;
    private String appName;

    public LogRequest(String level, String message, String appName) {
        this.level = level;
        this.message = message;
        this.appName = appName;
    }

    public String toJson() {
        return String.format(
                "{\"level\":\"%s\",\"message\":\"%s\",\"appName\":\"%s\"}",
                level, message, appName
        );
    }
}