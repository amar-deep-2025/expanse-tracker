package com.amar.fullstack.expanse_tracker_backend.controller;

import com.amar.fullstack.expanse_tracker_backend.dtos.AuthRequest;
import com.amar.fullstack.expanse_tracker_backend.dtos.AuthResponse;
import com.amar.fullstack.expanse_tracker_backend.dtos.RegisterRequest;
import com.amar.fullstack.expanse_tracker_backend.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {

        logger.info("Register API called");
        logger.debug("Register request received for email: {}", request.getEmail());
        authService.register(request);
        logger.info("User registered successfully");
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        logger.info("Login API called");
        logger.debug("Login attempt for email: {}", authRequest.getEmail());
        AuthResponse response = authService.login(authRequest);
        logger.info("User logged in successfully");
        return ResponseEntity.ok(response);
    }
}