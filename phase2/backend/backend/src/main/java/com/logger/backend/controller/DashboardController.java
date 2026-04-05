package com.logger.backend.controller;

import com.logger.backend.model.LogEntity;
import com.logger.backend.model.User;
import com.logger.backend.service.LogService;
import com.logger.backend.repository.UserRepository;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dashboard")
@CrossOrigin
public class DashboardController {

    private final LogService logService;
    private final UserRepository userRepository;

    // 🔥 CONSTRUCTOR (IMPORTANT)
    public DashboardController(LogService logService, UserRepository userRepository) {
        this.logService = logService;
        this.userRepository = userRepository;
    }

    // 🔥 GET LOGS (RBAC handled inside service)
    @GetMapping("/logs")
    public List<LogEntity> getLogs(
            @RequestHeader(value = "Authorization", required = false) String header
    ) {
        return logService.getLogsByApiKey(header);
    }

    // 🔥 GET USER (FOR FRONTEND ROLE DISPLAY)
    @GetMapping("/me")
    public User getUser(
            @RequestHeader(value = "Authorization", required = false) String header
    ) {
        if (header == null) return null;

        String apiKey = header.replace("Bearer ", "").trim();

        return userRepository.findByApiKey(apiKey);
    }
}