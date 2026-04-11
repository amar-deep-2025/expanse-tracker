package com.amar.fullstack.expanse_tracker_backend.service;
import com.amar.fullstack.expanse_tracker_backend.entity.User;
import com.amar.fullstack.expanse_tracker_backend.exception.ResourceNotFoundException;
import com.amar.fullstack.expanse_tracker_backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepo;

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
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
}