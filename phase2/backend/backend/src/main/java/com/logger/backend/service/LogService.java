package com.logger.backend.service;

import com.logger.backend.dto.LogRequest;
import com.logger.backend.model.LogEntity;
import com.logger.backend.model.User;
import com.logger.backend.repository.LogRepository;
import com.logger.backend.repository.UserRepository;
import com.logger.backend.util.MapperUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LogService {

    private final LogRepository logRepository;
    private final UserRepository userRepository;

    public LogService(LogRepository logRepository, UserRepository userRepository) {
        this.logRepository = logRepository;
        this.userRepository = userRepository;
    }

    // 🔥 SAVE LOGS
    public void processLogs(List<LogRequest> logs, String apiKey) {

        System.out.println("📦 Logs received");

        List<LogEntity> entities = new ArrayList<>();

        for (LogRequest log : logs) {
            LogEntity entity = MapperUtil.toEntity(log, apiKey);
            entities.add(entity);

            System.out.println("Saved: " + log.getMessage());
        }

        logRepository.saveAll(entities);
    }

    // 🔥 RBAC FETCH
    public List<LogEntity> getLogsByApiKey(String apiKeyHeader) {

        String apiKey = apiKeyHeader.replace("Bearer ", "");

        User user = userRepository.findByApiKey(apiKey);

        if (user == null) throw new RuntimeException("Invalid API Key");

        if (user.getRole().equals("ADMIN")) {
            return logRepository.findAll();
        }

        if (user.getRole().equals("MANAGER")) {
            return logRepository.findByProjectId(user.getProjectId());
        }

        if (user.getRole().equals("DEVELOPER")) {
            return logRepository.findByModuleId(user.getModuleId());
        }

        return List.of();
    }
}