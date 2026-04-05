package com.logger.backend.controller;

import com.logger.backend.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/logs")
public class LogController {

    private final LogService logService;

    @Autowired
    public LogController(LogService logService) {
        this.logService = logService;
    }

    // ✅ POST API
    @PostMapping
    public String receiveLogs(
            @RequestBody List<Map<String, Object>> logs,
            @RequestHeader(value = "Authorization", required = false) String apiKey
    ) {

        if (logs == null || logs.isEmpty()) {
            return "No logs received";
        }

        if (apiKey == null || apiKey.isEmpty()) {
            return "Missing API Key";
        }

        logService.processLogs(logs, apiKey);
        return "Logs processed successfully";
    }

    // ✅ GET API (test this in browser)
    @GetMapping("/health")
    public String healthCheck() {
        return "Backend is running";
    }
}