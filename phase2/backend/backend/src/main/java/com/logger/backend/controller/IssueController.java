package com.logger.backend.controller;

import com.logger.backend.model.Issue;
import com.logger.backend.model.User;
import com.logger.backend.repository.IssueRepository;
import com.logger.backend.repository.SubModuleRepository;
import com.logger.backend.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/issues")
public class IssueController {

    private final IssueRepository issueRepository;
    private final UserRepository userRepository;
    private final SubModuleRepository subModuleRepository;

    public IssueController(IssueRepository issueRepository,
                            UserRepository userRepository,
                            SubModuleRepository subModuleRepository) {
        this.issueRepository = issueRepository;
        this.userRepository = userRepository;
        this.subModuleRepository = subModuleRepository;
    }

    // Get issues — RBAC-aware
    @GetMapping
    public ResponseEntity<?> getIssues(Authentication auth,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "20") int size) {
        if (auth == null || !(auth.getPrincipal() instanceof User user)) {
            return ResponseEntity.status(401).build();
        }

        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Issue> issues;

        if (user.getRole() == User.Role.GLOBAL_ADMIN) {
            issues = issueRepository.findAll(pageable);
        } else if (user.getRole() == User.Role.PROJECT_MANAGER) {
            issues = issueRepository.findAll(pageable); // scope to project later
        } else {
            // DEVELOPER: only issues from their sub-modules
            List<String> subModuleIds = subModuleRepository.findSubModuleIdsByUserId(user.getId());
            if (subModuleIds.isEmpty()) {
                return ResponseEntity.ok(Page.empty(pageable));
            }
            issues = issueRepository.findBySubModuleIdIn(subModuleIds, pageable);
        }

        return ResponseEntity.ok(issues);
    }

    // Assign an issue to a developer (Manager only)
    @PatchMapping("/{id}/assign")
    public ResponseEntity<?> assignIssue(Authentication auth,
                                          @PathVariable Long id,
                                          @RequestBody Map<String, Long> body) {
        if (auth == null || !(auth.getPrincipal() instanceof User user)) {
            return ResponseEntity.status(401).build();
        }
        if (user.getRole() == User.Role.DEVELOPER) {
            return ResponseEntity.status(403).body("Only managers can assign issues");
        }

        Optional<Issue> issueOpt = issueRepository.findById(id);
        if (issueOpt.isEmpty()) return ResponseEntity.notFound().build();

        Issue issue = issueOpt.get();
        Long devId = body.get("developerId");
        if (devId != null) {
            userRepository.findById(devId).ifPresent(dev -> {
                issue.setAssignedTo(dev);
                issue.setStatus(Issue.IssueStatus.IN_PROGRESS);
            });
        }
        return ResponseEntity.ok(issueRepository.save(issue));
    }

    // Update issue status — Developer can resolve their own assigned issues
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(Authentication auth,
                                           @PathVariable Long id,
                                           @RequestBody Map<String, String> body) {
        if (auth == null || !(auth.getPrincipal() instanceof User user)) {
            return ResponseEntity.status(401).build();
        }

        Optional<Issue> issueOpt = issueRepository.findById(id);
        if (issueOpt.isEmpty()) return ResponseEntity.notFound().build();

        Issue issue = issueOpt.get();

        // Developer can only update issues assigned to them
        if (user.getRole() == User.Role.DEVELOPER) {
            if (issue.getAssignedTo() == null || !issue.getAssignedTo().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body("Cannot update issue not assigned to you");
            }
        }

        String newStatus = body.get("status");
        try {
            Issue.IssueStatus status = Issue.IssueStatus.valueOf(newStatus);
            issue.setStatus(status);
            if (status == Issue.IssueStatus.RESOLVED) {
                issue.setResolvedAt(LocalDateTime.now());
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid status: " + newStatus);
        }

        return ResponseEntity.ok(issueRepository.save(issue));
    }

    // Get single issue
    @GetMapping("/{id}")
    public ResponseEntity<?> getIssue(@PathVariable Long id, Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).build();
        return issueRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
