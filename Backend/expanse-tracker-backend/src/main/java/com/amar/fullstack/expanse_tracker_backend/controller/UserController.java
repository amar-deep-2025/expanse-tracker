package com.amar.fullstack.expanse_tracker_backend.controller;

import com.amar.fullstack.expanse_tracker_backend.entity.User;
import com.amar.fullstack.expanse_tracker_backend.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(Authentication auth){
        logger.info("Get current user API called");
        User user = (User) auth.getPrincipal();
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id){
        logger.info("Get user by id API called: {}", id);
        return ResponseEntity.ok(userService.getById(id));
    }

    @PostMapping("/upload-image")
    public ResponseEntity<?> uploadProfile(
            @RequestParam("file") MultipartFile file,
            Authentication auth) throws IOException {
        logger.info("Upload profile image API called");
        User user = (User) auth.getPrincipal();
        String email = user.getEmail();
        logger.debug("Uploading profile image for user");
        userService.uploadProfileImage(email, file);
        logger.info("Profile image updated successfully");
        return ResponseEntity.ok("Profile updated successfully");
    }
}