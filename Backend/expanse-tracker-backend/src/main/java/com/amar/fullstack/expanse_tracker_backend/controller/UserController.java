package com.amar.fullstack.expanse_tracker_backend.controller;

import com.amar.fullstack.expanse_tracker_backend.dtos.EmailChangeRequest;
import com.amar.fullstack.expanse_tracker_backend.dtos.PasswordChangeRequestDto;
import com.amar.fullstack.expanse_tracker_backend.dtos.UpdateProfileRequest;
import com.amar.fullstack.expanse_tracker_backend.dtos.UserResponseDto;
import com.amar.fullstack.expanse_tracker_backend.entity.User;
import com.amar.fullstack.expanse_tracker_backend.service.UserService;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser(Authentication auth){
        User user = (User) auth.getPrincipal();

        return ResponseEntity.ok(userService.getCurrentUser(user));
    }

    @GetMapping()
    public ResponseEntity<List<UserResponseDto>> findAll(){
        return ResponseEntity.ok(userService.getAllUsers());
    }
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getById(@PathVariable Long id){
        return ResponseEntity.ok(userService.getById(id));
    }

    @PostMapping("/upload-image")
    public ResponseEntity<Map<String,String>> uploadProfile(
            @RequestParam("file") MultipartFile file,
            Authentication auth) throws IOException {

        User user = (User) auth.getPrincipal();
        userService.uploadProfileImage(user.getEmail(), file);

        return ResponseEntity.ok(
                Map.of("message", "Image uploaded successfully")
        );
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<UserResponseDto> editRole(@PathVariable Long id,
                                         @RequestParam String role){
        return ResponseEntity.ok(userService.updateUserRole(id, role));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponseDto> updateProfile(
            @RequestBody UpdateProfileRequest request,
            Authentication auth) {

        User user = (User) auth.getPrincipal();
        return ResponseEntity.ok(userService.updateProfile(user.getId(), request));
    }

    @PatchMapping("/me/change-email")
    public ResponseEntity<?> changeEmail(
            @Valid @RequestBody EmailChangeRequest request,
            Authentication auth){

        User user = (User) auth.getPrincipal();

        userService.changeEmail(user.getId(), request);

        return ResponseEntity.ok("OTP sent to new email");
    }

    @PatchMapping("/me/verify-email")
    public ResponseEntity<UserResponseDto> verifyEmail(
            @RequestParam String otp,
            Authentication auth){

        User user = (User) auth.getPrincipal();

        return ResponseEntity.ok(
                userService.verifyEmailChange(user.getId(), otp)
        );
    }
    @PatchMapping("/me/change-password")
    public ResponseEntity<Map<String,String>> changePassword(
            @Valid @RequestBody PasswordChangeRequestDto request,
            Authentication auth) {

        User user = (User) auth.getPrincipal();

        userService.changePassword(user.getId(), request);

        return ResponseEntity.ok(
                Map.of("message","OTP sent to email")
        );
    }

    @PatchMapping("/me/verify-password")
    public ResponseEntity<Map<String, String>> verifyPassword(
            @RequestParam String otp,
            Authentication auth){

        User user = (User) auth.getPrincipal();

        userService.verifyPasswordChange(user.getId(), otp);

        return ResponseEntity.ok(
                Map.of("message", "Password changed successfully")
        );
    }

    @DeleteMapping("/me")
    public ResponseEntity<Map<String,String>> deleteCurrentUser(
            @RequestParam String password,
            Authentication auth) {

        User user = (User) auth.getPrincipal();

        userService.deleteCurrentUser(user.getId(), password);

        return ResponseEntity.ok(
                Map.of("message","User account deleted successfully")
        );
    }
}