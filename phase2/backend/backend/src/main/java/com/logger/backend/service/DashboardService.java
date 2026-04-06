package com.logger.backend.service;

import com.logger.backend.model.LogEntity;
import com.logger.backend.model.User;
import com.logger.backend.repository.LogRepository;
import com.logger.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final LogRepository logRepository;
    private final UserRepository userRepository;

    public DashboardService(LogRepository logRepository, UserRepository userRepository) {
        this.logRepository = logRepository;
        this.userRepository = userRepository;
    }

    public User getUser(String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing Authorization header");
        }

        String apiKey = authHeader.replace("Bearer ", "").trim();

        User user = userRepository.findByApiKey(apiKey);

        if (user == null) {
            throw new RuntimeException("Invalid API key");
        }

        return user;
    }

    // 🔥 LOGS
    public List<LogEntity> getLogs(String authHeader) {

        // If no auth header, return all logs (unauthenticated admin view)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return logRepository.findAll();
        }

        User user = getUser(authHeader);
        String role = user.getRole();

        if ("ADMIN".equalsIgnoreCase(role)) {
            return logRepository.findAll();
        }

        if ("MANAGER".equalsIgnoreCase(role)) {
            return logRepository.findByProjectId(user.getProjectId());
        }

        if ("DEVELOPER".equalsIgnoreCase(role)) {
            return logRepository.findByProjectIdAndModuleId(
                    user.getProjectId(),
                    user.getModuleId()
            );
        }

        throw new RuntimeException("Invalid role");
    }

    // 🔥 SUMMARY
    public Map<String, Long> getSummary(String authHeader) {
        return getLogs(authHeader).stream()
                .collect(Collectors.groupingBy(
                        LogEntity::getLevel,
                        Collectors.counting()
                ));
    }

    // 🔥 SIDEBAR
    public Set<String> getSidebar(String authHeader) {
        return getLogs(authHeader).stream()
                .map(LogEntity::getProjectId)
                .collect(Collectors.toSet());
    }
}