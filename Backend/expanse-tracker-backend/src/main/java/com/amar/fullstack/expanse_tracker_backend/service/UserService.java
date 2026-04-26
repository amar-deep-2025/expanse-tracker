package com.amar.fullstack.expanse_tracker_backend.service;
import com.amar.fullstack.expanse_tracker_backend.entity.NotificationType;
import com.amar.fullstack.expanse_tracker_backend.exception.UnAuthorizedException;
import com.amar.fullstack.expanse_tracker_backend.mapping.UserMapper;
import com.amar.fullstack.expanse_tracker_backend.dtos.*;
import com.amar.fullstack.expanse_tracker_backend.entity.Role;
import com.amar.fullstack.expanse_tracker_backend.entity.User;
import com.amar.fullstack.expanse_tracker_backend.exception.ResourceNotFoundException;
import com.amar.fullstack.expanse_tracker_backend.notification.service.NotificationService;
import com.amar.fullstack.expanse_tracker_backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepo;

    private PasswordEncoder passwordEncoder;

    private final OtpService otpService;

    private final StringRedisTemplate redisTemplate;
    private final NotificationService notificationService;
    public UserService(UserRepository userRepo,PasswordEncoder passwordEncoder, OtpService otpService, StringRedisTemplate redisTemplate, NotificationService notificationService) {
        this.userRepo = userRepo;
        this.passwordEncoder=passwordEncoder;
        this.otpService=otpService;
        this.redisTemplate=redisTemplate;
        this.notificationService=notificationService;
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

    public void changeEmail(Long userId, EmailChangeRequest request) {

        String newEmail = request.getNewEmail().trim().toLowerCase();

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getEmail().equalsIgnoreCase(newEmail)) {
            throw new IllegalArgumentException("New email cannot be same as current email");
        }

        if (userRepo.existsByEmail(newEmail)) {
            throw new IllegalArgumentException("Email already in use");
        }

        String otp = otpService.generateOtp();

        otpService.saveOtp("EMAIL_CHANGE_OTP:" + userId, otp);

        redisTemplate.opsForValue().set("EMAIL_CHANGE_NEW_EMAIL:" + userId, newEmail, 5, TimeUnit.MINUTES);

        NotificationRequest notify = new NotificationRequest();
        notify.setSubject("Email Change OTP");
        notify.setEmail(newEmail);
        notify.setMessage(
                "Hello,\n\n" +
                        "You have requested to perform a secure action on your Expanse Tracker account.\n\n" +
                        "Your One-Time Password (OTP) is:\n\n" +
                        otp + "\n\n" +
                        "This OTP is valid for the next 5 minutes. Please do not share it with anyone.\n\n" +
                        "If you did not initiate this request, please ignore this email or contact support immediately.\n\n" +
                        "----------------------------------\n" +
                        "This is a system-generated email. Please do not reply to this message.\n\n" +
                        "Regards,\n" +
                        "Expanse Tracker Team"
        );
        notify.setTypes(List.of(
                NotificationType.EMAIL
        ));
        notificationService.send(notify);
    }

    public UserResponseDto verifyEmailChange(Long userId, String otp) {

        boolean isValid = otpService.verifyOtp("EMAIL_CHANGE_OTP:" + userId, otp);

        if (!isValid) {
            throw new IllegalArgumentException("Invalid or expired OTP");
        }

        String newEmail = redisTemplate.opsForValue()
                .get("EMAIL_CHANGE_NEW_EMAIL:" + userId);

        if (newEmail == null) {
            throw new IllegalArgumentException("Request expired");
        }

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setEmail(newEmail);
        User updatedUser = userRepo.save(user);

        redisTemplate.delete("EMAIL_CHANGE_NEW_EMAIL:" + userId);

        return UserMapper.toDto(updatedUser);
    }

    public void changePassword(Long userId, PasswordChangeRequestDto request) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        if (request.getOldPassword().equals(request.getNewPassword())) {
            throw new IllegalArgumentException("New password must be different from old password");
        }

        String otp = otpService.generateOtp();

        String encodedPassword = passwordEncoder.encode(request.getNewPassword());

        otpService.saveOtp("PASSWORD_CHANGE_OTP:" + userId, otp);
        redisTemplate.opsForValue().set("PASSWORD_CHANGE_NEW_PASSWORD:" + userId,
                encodedPassword, 5, TimeUnit.MINUTES);

        NotificationRequest notify = new NotificationRequest();
        notify.setSubject("Password Change OTP");
        notify.setEmail(user.getEmail());
        notify.setMessage(
                "Hello,\n\n" +
                        "Your One-Time Password (OTP) is:\n\n" +
                        otp + "\n\n" +
                        "This OTP is valid for 5 minutes. Please do not share it with anyone.\n\n" +
                        "If you did not request this, please ignore this message.\n\n" +
                        "----------------------------------\n" +
                        "This is a system-generated email. Please do not reply.\n\n" +
                        "Regards,\n" +
                        "Expanse Tracker Team"
        );
        notify.setTypes(List.of(
                NotificationType.EMAIL
        ));
        notificationService.send(notify);
    }

    public void verifyPasswordChange(Long userId, String otp) {

        boolean isValid = otpService.verifyOtp("PASSWORD_CHANGE_OTP:" + userId, otp);

        if (!isValid) {
            throw new IllegalArgumentException("Invalid or expired OTP");
        }

        String newPassword = redisTemplate.opsForValue()
                .get("PASSWORD_CHANGE_NEW_PASSWORD:" + userId);

        if (newPassword == null) {
            throw new IllegalArgumentException("Request expired");
        }

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setPassword(newPassword);
        userRepo.save(user);

        redisTemplate.delete("PASSWORD_CHANGE_NEW_PASSWORD:" + userId);

        NotificationRequest notify = new NotificationRequest();
        notify.setSubject("Password Changed Successfully");
        notify.setEmail(user.getEmail());
        notify.setMessage(
                "Hello,\n\n" +
                        "Your password has been changed successfully for your Expanse Tracker account.\n\n" +
                        "If you did not perform this action, please contact support immediately.\n\n" +
                        "----------------------------------\n" +
                        "This is a system-generated email. Please do not reply to this message.\n\n" +
                        "Regards,\n" +
                        "Expanse Tracker Team"
        );
        notify.setTypes(List.of(
                NotificationType.EMAIL
        ));

        notificationService.send(notify);
    }

    public void deleteCurrentUser(Long userId, String password) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        userRepo.delete(user);
    }
}