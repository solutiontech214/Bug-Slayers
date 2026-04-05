package com.logger.backend.controller;

import com.logger.backend.dto.LogRequest;
import com.logger.backend.service.LogService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/logs")
public class LogController {

    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    @PostMapping
    public String receiveLogs(
            @RequestBody List<LogRequest> logs,
            @RequestHeader(value = "Authorization", required = false) String apiKey
    ) {
        logService.processLogs(logs, apiKey);
        return "Logs received";
    }
}