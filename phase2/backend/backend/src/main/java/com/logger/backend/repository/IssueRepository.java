package com.logger.backend.repository;

import com.logger.backend.model.Issue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, Long> {
    Page<Issue> findByProjectId(String projectId, Pageable pageable);
    Page<Issue> findBySubModuleIdIn(List<String> subModuleIds, Pageable pageable);
    Page<Issue> findByAssignedToId(Long userId, Pageable pageable);
    long countByStatus(Issue.IssueStatus status);
    long countByStatusAndProjectId(Issue.IssueStatus status, String projectId);
}
