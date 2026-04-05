package com.logger.backend.service;

import com.logger.backend.dto.LogRequest;
import com.logger.backend.model.Issue;
import com.logger.backend.model.LogEntity;
import com.logger.backend.model.User;
import com.logger.backend.repository.IssueRepository;
import com.logger.backend.repository.LogRepository;
import com.logger.backend.repository.SubModuleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class LogService {

    private final LogRepository logRepository;
    private final IssueRepository issueRepository;
    private final SubModuleRepository subModuleRepository;

    public LogService(LogRepository logRepository,
                      IssueRepository issueRepository,
                      SubModuleRepository subModuleRepository) {
        this.logRepository = logRepository;
        this.issueRepository = issueRepository;
        this.subModuleRepository = subModuleRepository;
    }

    @Transactional
    public LogEntity saveLog(LogRequest request) {
        LogEntity entity = new LogEntity();
        entity.setMessage(request.getMessage());
        entity.setLevel(request.getLevel() != null ? request.getLevel().toUpperCase() : "INFO");
        entity.setTimestamp(request.getTimestamp());

        // Extract metadata fields
        Map<String, Object> meta = request.getMetadata();
        if (meta != null) {
            entity.setEnvironment(getString(meta, "environment"));
            entity.setProjectId(getString(meta, "projectId"));
            entity.setModuleId(getString(meta, "moduleId"));
            entity.setSubModuleId(getString(meta, "subModuleId"));
        }

        entity.setStackTrace(request.getStackTrace());

        LogEntity saved = logRepository.save(entity);

        // Update PostgreSQL FTS search vector after insert
        try {
            logRepository.updateSearchVector(saved.getId());
        } catch (Exception e) {
            // FTS update is best-effort; don't fail the whole request
            System.out.println("Warning: FTS vector update failed: " + e.getMessage());
        }

        // Auto-create Issue for ERROR/FATAL logs
        String level = entity.getLevel();
        if ("ERROR".equals(level) || "FATAL".equals(level)) {
            createIssueIfNeeded(saved);
        }

        return saved;
    }

    private void createIssueIfNeeded(LogEntity log) {
        try {
            Issue issue = new Issue();
            issue.setTitle(truncate(log.getMessage(), 200));
            issue.setDescription(log.getMessage());
            issue.setSeverity(log.getLevel());
            issue.setProjectId(log.getProjectId());
            issue.setSubModuleId(log.getSubModuleId());
            issue.setStatus(Issue.IssueStatus.OPEN);
            issue.setLogId(log.getId());
            issue.setStackTrace(log.getStackTrace());
            issueRepository.save(issue);
        } catch (Exception e) {
            System.out.println("Warning: Issue creation failed: " + e.getMessage());
        }
    }

    // ---- Querying methods (RBAC-aware) ----

    public Page<LogEntity> getLogs(User currentUser, String level, String search,
                                    int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());

        if (currentUser.getRole() == User.Role.GLOBAL_ADMIN) {
            return logRepository.findAll(pageable);
        }

        if (currentUser.getRole() == User.Role.PROJECT_MANAGER) {
            // TODO: scope to their managed project — for now return all
            return logRepository.findAll(pageable);
        }

        // DEVELOPER: scope to submodules of their teams
        List<String> subModuleIds = subModuleRepository.findSubModuleIdsByUserId(currentUser.getId());
        if (subModuleIds.isEmpty()) {
            return Page.empty(pageable);
        }
        return logRepository.findBySubModuleIdIn(subModuleIds, pageable);
    }

    public List<LogEntity> searchLogs(User currentUser, String query, int limit, int offset) {
        String subModuleId = null;
        if (currentUser.getRole() == User.Role.DEVELOPER) {
            List<String> ids = subModuleRepository.findSubModuleIdsByUserId(currentUser.getId());
            subModuleId = ids.isEmpty() ? "NONE" : ids.get(0);
        }
        return logRepository.fullTextSearch(query, subModuleId, limit, offset);
    }

    public List<Object[]> getLogStats(User currentUser) {
        return logRepository.countByLevel();
    }

    // ---- Helpers ----

    private String getString(Map<String, Object> map, String key) {
        Object v = map.get(key);
        return v != null ? v.toString() : null;
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() > max ? s.substring(0, max) : s;
    }
}