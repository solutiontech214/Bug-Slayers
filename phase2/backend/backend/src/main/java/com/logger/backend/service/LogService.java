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

        String apiKey = apiKeyHeader.replace("Bearer ", "").trim();

        User user = userRepository.findByApiKey(apiKey);



        if (user == null) {
            System.out.println("❌ API key not found: " + apiKey);
            return logRepository.findAll(); // fallback (VERY IMPORTANT)
        }

        if ("ADMIN".equals(user.getRole())) {
            return logRepository.findAll();
        }

        if ("MANAGER".equals(user.getRole())) {
            return logRepository.findByProjectId(user.getProjectId());
        }

        if ("DEVELOPER".equals(user.getRole())) {
            return logRepository.findByModuleId(user.getModuleId());
        }

        return List.of();

    }
}