package com.example.demo.service;

import com.example.demo.entity.Log;
import com.example.demo.repository.LogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogService {

    private final LogRepository logRepository;

    public LogService(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    // Save log
    public Log saveLog(Log log) {
        return logRepository.save(log);
    }

    // Get all logs
    public List<Log> getAllLogs() {
        return logRepository.findAll();
    }
    public List<Log> saveAll(List<Log> logs) {
        return logRepository.saveAll(logs);
    }
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
}