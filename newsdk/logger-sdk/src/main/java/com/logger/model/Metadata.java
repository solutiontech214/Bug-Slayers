package com.logger.model;

public class Metadata {

    private final String projectId;
    private final String moduleId;
    private final String subModuleId;
    private final String environment;
    private final long timestamp;

    public Metadata(String projectId,
                    String moduleId,
                    String subModuleId,
                    String environment,
                    long timestamp) {
        this.projectId = projectId;
        this.moduleId = moduleId;
        this.subModuleId = subModuleId;
        this.environment = environment;
        this.timestamp = timestamp;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getModuleId() {
        return moduleId;
    }

    public String getSubModuleId() {
        return subModuleId;
    }

    public String getEnvironment() {
        return environment;
    }

    public long getTimestamp() {
        return timestamp;
    }
}