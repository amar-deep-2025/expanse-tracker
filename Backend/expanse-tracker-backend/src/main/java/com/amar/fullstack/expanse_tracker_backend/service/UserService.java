package com.amar.fullstack.expanse_tracker_backend.service;
import com.amar.fullstack.expanse_tracker_backend.Mapping.UserMapper;
import com.amar.fullstack.expanse_tracker_backend.dtos.EmailChangeRequest;
import com.amar.fullstack.expanse_tracker_backend.dtos.PasswordChangeRequestDto;
import com.amar.fullstack.expanse_tracker_backend.dtos.UpdateProfileRequest;
import com.amar.fullstack.expanse_tracker_backend.dtos.UserResponseDto;
import com.amar.fullstack.expanse_tracker_backend.entity.Role;
import com.amar.fullstack.expanse_tracker_backend.entity.User;
import com.amar.fullstack.expanse_tracker_backend.exception.ResourceNotFoundException;
import com.amar.fullstack.expanse_tracker_backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepo;

    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepo,PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder=passwordEncoder;
    }

    public Optional<User> getCurrentUser(String email) {

        logger.info("Fetching current user");

        return userRepo.findByEmail(email)
                .map(user -> {
                    logger.debug("User found");
                    return user;
                })
                .or(() -> {
                    logger.warn("User not found");
                    return Optional.empty();
                });
    }

    public List<User> getAllUsers(){
        logger.info("Fetching all users");
        List<User> users = userRepo.findAll();
        logger.debug("Total users found: {}", users.size());
        return users;
    }

    public User getById(Long id) {

        logger.info("Fetching user by id: {}", id);

        return userRepo.findById(id)
                .orElseThrow(() -> {
                    logger.warn("User not found with id: {}", id);
                    return new ResourceNotFoundException("User not found");
                });
    }

    public void uploadProfileImage(String email, MultipartFile file) throws IOException {

        logger.info("Uploading profile image");

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("User not found");
                    return new ResourceNotFoundException("User not found");
                });

        if (file.isEmpty()) {
            logger.warn("Empty file upload attempt");
            throw new RuntimeException("File is empty");
        }

        if (file.getSize() > 2 * 1024 * 1024) {
            logger.warn("File size exceeds limit");
            throw new RuntimeException("File size exceeds 2MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            logger.warn("Invalid file type upload attempt");
            throw new RuntimeException("Only image files are allowed");
        }

        String fileName = System.currentTimeMillis() + "_" +
                file.getOriginalFilename().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");

        String uploadDir = System.getProperty("user.dir") + "/uploads/";
        File folder = new File(uploadDir);

        if (!folder.exists()) {
            folder.mkdirs();
            logger.debug("Upload directory created");
        }

        try {
            file.transferTo(new File(uploadDir + fileName));
            logger.info("Profile image uploaded successfully");
        } catch (IOException e) {
            logger.error("File upload failed", e);
            throw new RuntimeException("File upload failed");
        }
        user.setProfileImage(fileName);
        userRepo.save(user);

        logger.info("User profile updated successfully");
    }

    public User updateUserRole(Long userId, String role) {
        logger.info("Entered updateUserRole method with userId:{} and role: {}", userId, role);
        User existingUser = userRepo.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("User not found with id: {}", userId);
                    return new ResourceNotFoundException("User not found with id: " + userId);
                });
        Role newRole;
        try {
            newRole = Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid role provided: {}", role);
            throw new IllegalArgumentException("Invalid role. Allowed roles: USER, ADMIN");
        }
        existingUser.setRole(newRole);
        User savedUser = userRepo.save(existingUser);
        logger.info("User role updated successfully for userId: {} to role: {}",
                savedUser.getId(),
                savedUser.getRole());

        return savedUser;
    }

    public User updateProfile(Long userId, UpdateProfileRequest request){
        logger.info("Entered UpdateProfile method with userId: {}", userId);
        User user=userRepo.findById(userId).orElseThrow(()->{
            logger.warn("User not found with id: {}", userId);
            return new ResourceNotFoundException("User not Found with id: "+userId);
        });
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        logger.info("User profile updated successfully for userId: {}", userId);
        return userRepo.save(user);
    }

    public UserResponseDto changeEmail(Long userId, EmailChangeRequest request) {
        logger.info("Change email requested for userId: {}", userId);
        String newEmail = request.getNewEmail().trim().toLowerCase();

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + userId));
        if (user.getEmail().equalsIgnoreCase(newEmail)) {
            logger.warn("Same email provided for userId: {}", userId);
            throw new IllegalArgumentException(
                    "New email cannot be same as current email");
        }
        if (userRepo.existsByEmail(newEmail)) {
            logger.warn("Email already in use");
            throw new IllegalArgumentException("Email already in use");
        }
        user.setEmail(newEmail);
        User updatedUser = userRepo.save(user);
        logger.info("Email changed successfully for userId: {}", userId);
        return UserMapper.toDto(updatedUser);
    }

    public void changePassword(Long userId, PasswordChangeRequestDto request){
        User user=userRepo.findById(userId).orElseThrow(()->new ResourceNotFoundException("User not found with id: "+userId));
        if (!passwordEncoder.matches(request.getOldPassword(),
                user.getPassword())){
            throw new IllegalArgumentException("Old password is incorrect");
        }
        if (request.getOldPassword().equals(request.getNewPassword())) {
            logger.warn("Same password attempt for userId: {}", userId);
            throw new IllegalArgumentException(
                    "New password must be different from old password");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepo.save(user);
    }

}