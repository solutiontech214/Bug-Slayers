package com.example.demo.controller;

import com.example.demo.entity.Log;
import com.example.demo.service.LogService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/logs")
public class LogController {

    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    // Single log
    @PostMapping
    public Log createLog(@RequestBody Log log) {
        return logService.saveLog(log);
    }

    // Get all logs
    @GetMapping
    public List<Log> getLogs() {
        return logService.getAllLogs();
    }

    // ✅ Batch logs (FIXED)
    @PostMapping("/batch")
    public List<Log> saveBatch(@RequestBody List<Log> logs) {
        return logService.saveAll(logs);
    }
    @GetMapping("/filter")
    public List<Log> filterLogs(
            @RequestParam(required = false) String appName,
            @RequestParam(required = false) String level
    ) {
        return logService.filterLogs(appName, level);
    }
}