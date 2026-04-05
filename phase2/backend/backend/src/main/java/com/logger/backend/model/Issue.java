package com.logger.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "issues")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IssueStatus status = IssueStatus.OPEN;

    @Column(nullable = false, length = 10)
    private String severity; // ERROR or FATAL

    @Column(name = "project_id", length = 100)
    private String projectId;

    @Column(name = "sub_module_id", length = 100)
    private String subModuleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_id")
    private User assignedTo;

    // The triggering log entry
    @Column(name = "log_id")
    private Long logId;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "stack_trace", columnDefinition = "TEXT")
    private String stackTrace;

    public enum IssueStatus {
        OPEN,
        IN_PROGRESS,
        RESOLVED
    }
}
