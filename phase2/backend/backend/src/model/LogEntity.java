package com.model;

import jakarta.persistence.*;

@Entity
@Table(name = "logs")
public class LogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String apiKey;
    private String projectId;
    private String moduleId;
    private String subModuleId;
    private String environment;

    private String level;

    @Column(length = 1000)
    private String message;

    @Column(length = 5000)
    private String stackTrace;

    private Long timestamp;

    @Column(length = 2000)
    private String extra; // JSON stored as string

    // 🔹 Default constructor (required by JPA)
    public LogEntity() {}

    // 🔹 Full constructor
    public LogEntity(String apiKey,
                     String projectId,
                     String moduleId,
                     String subModuleId,
                     String environment,
                     String level,
                     String message,
                     String stackTrace,
                     Long timestamp,
                     String extra) {

        this.apiKey = apiKey;
        this.projectId = projectId;
        this.moduleId = moduleId;
        this.subModuleId = subModuleId;
        this.environment = environment;
        this.level = level;
        this.message = message;
        this.stackTrace = stackTrace;
        this.timestamp = timestamp;
        this.extra = extra;
    }

    // 🔹 Getters and Setters

    public Long getId() { return id; }

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getModuleId() { return moduleId; }
    public void setModuleId(String moduleId) { this.moduleId = moduleId; }

    public String getSubModuleId() { return subModuleId; }
    public void setSubModuleId(String subModuleId) { this.subModuleId = subModuleId; }

    public String getEnvironment() { return environment; }
    public void setEnvironment(String environment) { this.environment = environment; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getStackTrace() { return stackTrace; }
    public void setStackTrace(String stackTrace) { this.stackTrace = stackTrace; }

    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }

    public String getExtra() { return extra; }
    public void setExtra(String extra) { this.extra = extra; }
}