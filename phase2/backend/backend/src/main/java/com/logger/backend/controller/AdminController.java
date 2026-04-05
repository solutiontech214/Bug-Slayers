package com.logger.backend.controller;

import com.logger.backend.model.User;
import com.logger.backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;

    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * GET /admin/users — List all users (Admin only)
     */
    @GetMapping("/users")
    @PreAuthorize("hasAuthority('GLOBAL_ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> listUsers() {

        List<Map<String, Object>> users = userRepository.findAll().stream()
                .map(u -> {
                    Map<String, Object> map = new java.util.HashMap<>();
                    map.put("id", u.getId());
                    map.put("name", u.getName());
                    map.put("email", u.getEmail());
                    map.put("role", u.getRole().name());
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(users);
    }

    /**
     * PATCH /admin/users/{id}/role — Update user role (Admin only)
     */
    @PatchMapping("/users/{id}/role")
    @PreAuthorize("hasAuthority('GLOBAL_ADMIN')")
    public ResponseEntity<?> updateUserRole(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return userRepository.findById(id).map(user -> {
            try {
                user.setRole(User.Role.valueOf(body.get("role")));
                userRepository.save(user);
                return ResponseEntity.ok(Map.of("message", "Role updated successfully"));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid role"));
            }
        }).orElse(ResponseEntity.notFound().build());
    }
}
