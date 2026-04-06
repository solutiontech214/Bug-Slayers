package com.logger.backend.controller;

import com.logger.backend.model.LogEntity;
import com.logger.backend.model.User;
import com.logger.backend.service.DashboardService;
import com.logger.backend.service.LogService;
import com.logger.backend.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dashboard")
@CrossOrigin
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/logs")
    public ResponseEntity<?> getLogs(
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        return ResponseEntity.ok(dashboardService.getLogs(authHeader));
    }

    @GetMapping("/summary")
    public ResponseEntity<?> getSummary(
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        return ResponseEntity.ok(dashboardService.getSummary(authHeader));
    }

    @GetMapping("/sidebar")
    public ResponseEntity<?> getSidebar(
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        return ResponseEntity.ok(dashboardService.getSidebar(authHeader));
    }
}