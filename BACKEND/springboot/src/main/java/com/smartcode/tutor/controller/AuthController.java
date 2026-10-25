
package com.smartcode.tutor.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartcode.tutor.dto.LoginRequest;
import com.smartcode.tutor.dto.RegisterRequest;
import com.smartcode.tutor.entity.User;
import com.smartcode.tutor.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(
            @Valid @RequestBody RegisterRequest request) {

        User user = authService.register(
                request.getUsername().trim(),
                request.getEmail().trim().toLowerCase(),
                request.getPassword()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(
                Map.of(
                        "success", true,
                        "message", "Registration successful.",
                        "userId", user.getId(),
                        "username", user.getUsername(),
                        "email", user.getEmail()
                )
        );
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @Valid @RequestBody LoginRequest request) {

        User user = authService.login(
                request.getUsernameOrEmail().trim(),
                request.getPassword()
        );

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "Login successful.",
                        "userId", user.getId(),
                        "username", user.getUsername(),
                        "email", user.getEmail()
                )
        );
    }
}
