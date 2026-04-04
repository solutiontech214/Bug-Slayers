package com.logger.model; 

public class LogEvent {

    private final String message;
    private final LogLevel level;
    private final Metadata metadata;
    private final String stackTrace; // only for ERROR/FATAL

    public LogEvent(String message,
                    LogLevel level,
                    Metadata metadata,
                    String stackTrace) {
        this.message = message;
        this.level = level;
        this.metadata = metadata;
        this.stackTrace = stackTrace;
    }

    public String getMessage() {
        return message;
    }

    public LogLevel getLevel() {
        return level;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    // 🔥 Convert to JSON (simple manual version)
    public String toJson() {
        StringBuilder sb = new StringBuilder();

        sb.append("{");
        sb.append("\"message\":\"").append(message).append("\",");
        sb.append("\"level\":\"").append(level).append("\",");

        sb.append("\"metadata\":{");
        sb.append("\"projectId\":\"").append(metadata.getProjectId()).append("\",");
        sb.append("\"moduleId\":\"").append(metadata.getModuleId()).append("\",");
        sb.append("\"subModuleId\":\"").append(metadata.getSubModuleId()).append("\",");
        sb.append("\"environment\":\"").append(metadata.getEnvironment()).append("\",");
        sb.append("\"timestamp\":").append(metadata.getTimestamp());
        sb.append("}");

        if (stackTrace != null) {
            sb.append(",\"stackTrace\":\"")
              .append(stackTrace.replace("\n", "\\n").replace("\"", "'"))
              .append("\"");
        }

        sb.append("}");

        return sb.toString();
    }
}
