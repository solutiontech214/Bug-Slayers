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

    // ✅ ONLY THIS (REMOVE @Autowired field duplication)
    public LogService(LogRepository logRepository, UserRepository userRepository) {
        this.logRepository = logRepository;
        this.userRepository = userRepository;
    }

    public void processLogs(List<LogRequest> logs, String apiKey) {

        List<LogEntity> entities = new ArrayList<>();

        for (LogRequest log : logs) {
            entities.add(MapperUtil.toEntity(log, apiKey));
        }

        logRepository.saveAll(entities);
    }

    public List<LogEntity> getLogsByApiKey(String apiKeyHeader) {

        System.out.println("Header: " + apiKeyHeader);

        if (apiKeyHeader == null) {
            return logRepository.findAll();
        }

        String apiKey = apiKeyHeader.replace("Bearer ", "").trim();

        System.out.println("Processed Key: " + apiKey);

        User user = userRepository.findByApiKey(apiKey);

        System.out.println("User: " + user);

        // 🔥 IMPORTANT FIX
        if (user == null) {
            System.out.println("❌ No user found");
            return logRepository.findAll();
        }

        System.out.println("Role: " + user.getRole());

        // 🔥 FORCE ADMIN CHECK FIRST
        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            System.out.println("✅ ADMIN access");
            return logRepository.findAll();
        }

        if ("MANAGER".equalsIgnoreCase(user.getRole())) {
            return logRepository.findByProjectId(user.getProjectId());
        }

        if ("DEVELOPER".equalsIgnoreCase(user.getRole())) {
            return logRepository.findByModuleId(user.getModuleId());
        }

        return logRepository.findAll(); // fallback
    }
}