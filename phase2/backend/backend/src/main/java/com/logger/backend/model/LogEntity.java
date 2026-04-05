package com.logger.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "logs")
public class LogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;
    private String level;
    private String stackTrace;

    private String projectId;
    private String moduleId;
    private String subModuleId;
    private String environment;

    private Long timestamp;

    // 🔥 GETTERS (IMPORTANT FOR JSON)

    public Long getId() { return id; }

    public String getMessage() { return message; }
    public String getLevel() { return level; }
    public String getStackTrace() { return stackTrace; }

    public String getProjectId() { return projectId; }
    public String getModuleId() { return moduleId; }
    public String getSubModuleId() { return subModuleId; }
    public String getEnvironment() { return environment; }

    public Long getTimestamp() { return timestamp; }

    // 🔥 SETTERS

    public void setMessage(String message) { this.message = message; }
    public void setLevel(String level) { this.level = level; }
    public void setStackTrace(String stackTrace) { this.stackTrace = stackTrace; }

    public void setProjectId(String projectId) { this.projectId = projectId; }
    public void setModuleId(String moduleId) { this.moduleId = moduleId; }
    public void setSubModuleId(String subModuleId) { this.subModuleId = subModuleId; }
    public void setEnvironment(String environment) { this.environment = environment; }

    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
}