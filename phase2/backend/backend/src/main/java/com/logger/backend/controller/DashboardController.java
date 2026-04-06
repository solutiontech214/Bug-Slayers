package com.logger.backend.controller;

import com.logger.backend.model.LogEntity;
import com.logger.backend.service.LogService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dashboard")
@CrossOrigin
public class DashboardController {

    private final LogService logService;

    public DashboardController(LogService logService) {
        this.logService = logService;
    }

    @GetMapping("/logs")
    public List<LogEntity> getLogs(
            @RequestHeader(value = "Authorization", required = false) String apiKey
    ) {
        if (apiKey == null) {
            apiKey = "Bearer sk_admin"; // default for testing
        }
        return logService.getLogsByApiKey(apiKey);
    }
}