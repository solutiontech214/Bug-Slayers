package com.logger.backend.controller;

import com.logger.backend.dto.LogRequest;
import com.logger.backend.model.LogEntity;
import com.logger.backend.model.User;
import com.logger.backend.service.LogService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/logs")
public class LogController {

    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
        System.out.println("🔥 LOG CONTROLLER LOADED");
    }

    @GetMapping("/health")
    public String health() {
        return "Log Aggregation Platform — RUNNING";
    }

    // ✅ SDK Ingestion — single log
    @PostMapping
    public ResponseEntity<String> receiveLog(@RequestBody LogRequest request,
                                              @RequestHeader(value = "Authorization", required = false) String authHeader) {
        System.out.println("📩 Log received: " + request.getMessage() + " [" + request.getLevel() + "]");
        logService.saveLog(request);
        return ResponseEntity.ok("Log saved successfully");
    }

    // ✅ SDK Ingestion — batch
    @PostMapping("/batch")
    public ResponseEntity<String> receiveBatch(@RequestBody List<LogRequest> requests,
                                                @RequestHeader(value = "Authorization", required = false) String authHeader) {
        System.out.println("📦 Batch logs received: " + requests.size());
        for (LogRequest request : requests) {
            logService.saveLog(request);
        }
        return ResponseEntity.ok("Batch of " + requests.size() + " logs saved");
    }

    // ✅ Get logs — RBAC-aware (frontend use)
    @GetMapping
    public ResponseEntity<?> getLogs(Authentication auth,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "50") int size,
                                      @RequestParam(required = false) String level,
                                      @RequestParam(required = false) String search) {
        if (auth == null || !(auth.getPrincipal() instanceof User user)) {
            return ResponseEntity.status(401).build();
        }

        if (search != null && !search.isBlank()) {
            List<LogEntity> results = logService.searchLogs(user, search, size, page * size);
            return ResponseEntity.ok(results);
        }

        Page<LogEntity> result = logService.getLogs(user, level, search, page, size);
        return ResponseEntity.ok(result);
    }

    // ✅ Log statistics (for dashboard)
    @GetMapping("/stats")
    public ResponseEntity<?> getStats(Authentication auth) {
        if (auth == null || !(auth.getPrincipal() instanceof User user)) {
            return ResponseEntity.status(401).build();
        }
        List<Object[]> rawStats = logService.getLogStats(user);
        Map<String, Long> stats = new HashMap<>();
        for (Object[] row : rawStats) {
            stats.put((String) row[0], (Long) row[1]);
        }
        return ResponseEntity.ok(stats);
    }
}