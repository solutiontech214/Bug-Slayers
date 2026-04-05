package com.logger.backend.service;

import com.logger.backend.model.LogEntity;
import com.logger.backend.model.User;
import com.logger.backend.repository.LogRepository;
import com.logger.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    private final LogRepository logRepository;
    private final UserRepository userRepository;

    public DashboardService(LogRepository logRepository, UserRepository userRepository) {
        this.logRepository = logRepository;
        this.userRepository = userRepository;
    }

    public List<LogEntity> getLogs(String authHeader) {

        if (authHeader == null) return logRepository.findAll();

        String apiKey = authHeader.replace("Bearer ", "").trim();

        User user = userRepository.findByApiKey(apiKey);

        if (user == null) return logRepository.findAll();

        String role = user.getRole();

        System.out.println("ROLE: " + role);

        // 🔥 ADMIN
        if ("ADMIN".equalsIgnoreCase(role)) {
            return logRepository.findAll();
        }

        // 🔥 MANAGER
        if ("MANAGER".equalsIgnoreCase(role)) {
            return logRepository.findByProjectId(user.getProjectId());
        }

        // 🔥 DEVELOPER
        if ("DEVELOPER".equalsIgnoreCase(role)) {
            return logRepository.findByModuleId(user.getModuleId());
        }

        return logRepository.findAll();
    }
}