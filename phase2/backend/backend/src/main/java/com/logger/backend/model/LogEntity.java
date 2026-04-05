package com.logger.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.Formula;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "logs",
    indexes = {
        @Index(name = "idx_logs_level", columnList = "level"),
        @Index(name = "idx_logs_sub_module_id", columnList = "sub_module_id"),
        @Index(name = "idx_logs_project_id", columnList = "project_id"),
        @Index(name = "idx_logs_timestamp", columnList = "timestamp")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    @Column(nullable = false, length = 10)
    private String level; // INFO, WARN, ERROR, FATAL

    private long timestamp;

    @Column(length = 50)
    private String environment;

    @Column(name = "project_id", length = 100)
    private String projectId;

    @Column(name = "module_id", length = 100)
    private String moduleId;

    @Column(name = "sub_module_id", length = 100)
    private String subModuleId;

    // Multi-line stack trace for ERROR/FATAL
    @Column(name = "stack_trace", columnDefinition = "TEXT")
    private String stackTrace;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // NOTE: GIN index on search_vector is created via SQL script (schema.sql)
    // PostgreSQL Full-Text Search vector column
    @Column(name = "search_vector", columnDefinition = "tsvector")
    private String searchVector;
}