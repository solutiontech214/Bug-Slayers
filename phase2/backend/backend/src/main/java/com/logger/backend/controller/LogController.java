package com.logger.backend.controller;

import com.logger.backend.dto.LogRequest;
import com.logger.backend.service.LogService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/logs")
public class LogController {

    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
        System.out.println("🔥 CONTROLLER LOADED");
    }

    @GetMapping("/health")
    public String health() {
        return "WORKING!!!";
    }

    // ✅ NEW: Receive logs from SDK
    @PostMapping
    public String receiveLog(@RequestBody LogRequest request,
                             @RequestHeader(value = "Authorization", required = false) String authHeader) {

        System.out.println("📩 Log received: " + request.getMessage());

        logService.saveLog(request);

        return "Log saved successfully";
    }
    // ✅ BATCH: Receive multiple logs from SDK
    @PostMapping("/batch")
    public String receiveLogs(@RequestBody java.util.List<LogRequest> requests,
                              @RequestHeader(value = "Authorization", required = false) String authHeader) {
        System.out.println("\uD83D\uDCE9 Batch logs received: " + requests.size());
        for (LogRequest request : requests) {
            logService.saveLog(request);
        }
        return "Batch logs saved successfully";
    }
}