package com.example.demo.controller;

import com.example.demo.entity.Log;
import com.example.demo.service.LogService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/logs")
public class LogController {

    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    // ✅ 1. Single log
    @PostMapping
    public Log createLog(@RequestBody Log log) {
        return logService.saveLog(log);
    }

    // ✅ 2. Batch logs
    @PostMapping("/batch")
    public List<Log> saveBatch(@RequestBody List<Log> logs) {
        return logService.saveAll(logs);
    }

    // ✅ 3. Get all logs (simple)
    @GetMapping("/all")
    public List<Log> getAllLogs() {
        return logService.getAllLogs();
    }

    // ✅ 4. Basic filter (old one, optional)
    @GetMapping("/filter")
    public List<Log> filterLogs(
            @RequestParam(required = false) String appName,
            @RequestParam(required = false) String level
    ) {
        return logService.filterLogs(appName, level);
    }

    // ✅ 5. ADVANCED API (pagination + sorting + date filter)
    @GetMapping
    public Page<Log> getLogs(
            @RequestParam(required = false) String appName,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        return logService.getFilteredLogs(
                appName,
                level,
                startDate,
                endDate,
                page,
                size,
                sortBy,
                direction
        );
    }
}