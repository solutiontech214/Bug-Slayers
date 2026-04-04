package com.example.demo.service;

import com.example.demo.entity.Log;
import com.example.demo.repository.LogRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LogService {

    private final LogRepository logRepository;

    public LogService(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    // ✅ 1. Save single log
    public Log saveLog(Log log) {
        return logRepository.save(log);
    }

    // ✅ 2. Save batch logs
    public List<Log> saveAll(List<Log> logs) {
        return logRepository.saveAll(logs);
    }

    // ✅ 3. Get all logs (simple)
    public List<Log> getAllLogs() {
        return logRepository.findAll();
    }

    // ✅ 4. Basic filter (old method)
    public List<Log> filterLogs(String appName, String level) {

        if (appName != null && level != null) {
            return logRepository.findByAppNameAndLevel(appName, level);
        }

        if (appName != null) {
            return logRepository.findByAppName(appName);
        }

        if (level != null) {
            return logRepository.findByLevel(level);
        }

        return logRepository.findAll();
    }

    // ✅ 5. ADVANCED FILTER (pagination + sorting + date range)
    public Page<Log> getFilteredLogs(
            String appName,
            String level,
            LocalDateTime startDate,
            LocalDateTime endDate,
            int page,
            int size,
            String sortBy,
            String direction
    ) {

        // Sorting
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        // 🔥 Case 1: All filters applied
        if (appName != null && level != null && startDate != null && endDate != null) {
            return logRepository.findByAppNameAndLevelAndTimestampBetween(
                    appName, level, startDate, endDate, pageable
            );
        }

        // 🔥 Case 2: Only date range
        if (startDate != null && endDate != null) {
            return logRepository.findByTimestampBetween(
                    startDate, endDate, pageable
            );
        }

        // 🔥 Default: return all with pagination
        return logRepository.findAll(pageable);
    }
}