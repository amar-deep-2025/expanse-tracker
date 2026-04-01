package com.amar.fullstack.expanse_tracker_backend.controller;

import com.amar.fullstack.expanse_tracker_backend.dtos.AuthRequest;
import com.amar.fullstack.expanse_tracker_backend.dtos.AuthResponse;
import com.amar.fullstack.expanse_tracker_backend.dtos.RegisterRequest;
import com.amar.fullstack.expanse_tracker_backend.service.AuthService;
import com.amar.fullstack.expanse_tracker_backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        // System.out.println("Name: " + request.getName());
        // System.out.println("Email: " + request.getEmail());
        // System.out.println("Password: " + request.getPassword());
        // System.out.println("Phone: " + request.getPhone());
        authService.register(request);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        AuthResponse response = authService.login(authRequest);
        return ResponseEntity.ok(response);
    }
}
