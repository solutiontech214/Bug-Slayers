package com.logger.config;

public class SDKConfig {

    private final String apiKey;
    private final String projectId;
    private final String moduleId;
    private String subModuleId; // can change dynamically
    private final String environment;
    private final String endpoint;

    // Optional advanced configs
    private final int batchSize;
    private final int flushIntervalSeconds;

    // 🔒 Private constructor (use builder)
    private SDKConfig(Builder builder) {
        this.apiKey = builder.apiKey;
        this.projectId = builder.projectId;
        this.moduleId = builder.moduleId;
        this.subModuleId = builder.subModuleId;
        this.environment = builder.environment;
        this.endpoint = builder.endpoint;
        this.batchSize = builder.batchSize;
        this.flushIntervalSeconds = builder.flushIntervalSeconds;
    }

    // 🔹 Getters
    public String getApiKey() {
        return apiKey;
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

    public String getEndpoint() {
        return endpoint;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public int getFlushIntervalSeconds() {
        return flushIntervalSeconds;
    }

    // 🔹 Allow runtime update (useful)
    public void setSubModuleId(String subModuleId) {
        this.subModuleId = subModuleId;
    }

    // 🏗️ BUILDER CLASS
    public static class Builder {

        private String apiKey;
        private String projectId;
        private String moduleId;
        private String subModuleId = "default";
        private String environment = "production";
        private String endpoint = "http://localhost:8080/logs";

        // Default batching config
        private int batchSize = 10;
        private int flushIntervalSeconds = 5;

        // 🔹 Required fields
        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public Builder projectId(String projectId) {
            this.projectId = projectId;
            return this;
        }

        public Builder moduleId(String moduleId) {
            this.moduleId = moduleId;
            return this;
        }

        // 🔹 Optional fields
        public Builder subModuleId(String subModuleId) {
            this.subModuleId = subModuleId;
            return this;
        }

        public Builder environment(String environment) {
            this.environment = environment;
            return this;
        }

        public Builder endpoint(String endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public Builder batchSize(int batchSize) {
            this.batchSize = batchSize;
            return this;
        }

        public Builder flushIntervalSeconds(int seconds) {
            this.flushIntervalSeconds = seconds;
            return this;
        }

        // 🔥 Build method with validation
        public SDKConfig build() {
            if (apiKey == null || apiKey.isEmpty()) {
                throw new IllegalArgumentException("API Key is required");
            }
            if (projectId == null || projectId.isEmpty()) {
                throw new IllegalArgumentException("Project ID is required");
            }
            if (moduleId == null || moduleId.isEmpty()) {
                throw new IllegalArgumentException("Module ID is required");
            }

            return new SDKConfig(this);
        }
    }
}