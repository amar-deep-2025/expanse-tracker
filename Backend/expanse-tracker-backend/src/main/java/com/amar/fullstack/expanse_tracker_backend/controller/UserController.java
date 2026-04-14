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

    @GetMapping()
    public ResponseEntity<List<User>> findAll(Authentication auth){
        logger.info("Get all users API called");
        User user = (User) auth.getPrincipal();
        List<User> allUsers= userService.getAllUsers();
        return ResponseEntity.ok(allUsers);
    }


    @GetMapping("/{id}")
    public ResponseEntity<User> getById(Authentication auth, @PathVariable Long id){
        logger.info("Get user by id API called: {}", id);
        User user = (User) auth.getPrincipal();
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
    @PatchMapping("/{id}/role")
    public ResponseEntity<?> EditRole(@PathVariable Long id,@RequestParam String role, Authentication auth){
        logger.info("Update user role API called for user id:{} with role: {}", id, role);
        User user = (User) auth.getPrincipal();
        User updateUser=userService.updateUserRole(id, role);
        logger.info("User role updated successfully for user id: {}", id);
        return ResponseEntity.ok(updateUser);

    }

    @PutMapping("/me")
    public ResponseEntity<User> updateProfile(
            @RequestBody UpdateProfileRequest request,
            Authentication auth) {
        logger.info("Update profile API called");
        User user = (User) auth.getPrincipal();
        User updatedUser = userService.updateProfile(user.getId(), request);
        logger.info("Profile updated successfully for user: {}", user.getEmail());
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/me/change-email")
    public ResponseEntity<UserResponseDto> changeEmail(
            @Valid @RequestBody EmailChangeRequest request,
            Authentication auth){
        logger.info("Change email API called");
        User user = (User) auth.getPrincipal();
        UserResponseDto response = userService.changeEmail(user.getId(), request);
        logger.info("Email changed successfully for user: {}", user.getEmail());
        return ResponseEntity.ok(response);

    }

    @PatchMapping("/me/change-password")
    public ResponseEntity<?> changePassword(
            @Valid @RequestBody PasswordChangeRequestDto request,
            Authentication auth) {
        logger.info("Change password API called");
        User user = (User) auth.getPrincipal();
        userService.changePassword(user.getId(), request);
        logger.info("Password changed successfully for user: {}", user.getEmail());
        return ResponseEntity.ok("Password changed successfully");
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteCurrentUser(Authentication auth) {

        User user = (User) auth.getPrincipal();

        logger.info("Delete request for logged-in user: {}", user.getId());

        userService.deleteCurrentUser(user);

        return ResponseEntity.noContent().build();
    }
}