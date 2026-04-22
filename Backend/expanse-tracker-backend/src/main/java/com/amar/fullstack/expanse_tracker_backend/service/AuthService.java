package com.amar.fullstack.expanse_tracker_backend.service;

import com.amar.fullstack.expanse_tracker_backend.config.JwtUtil;
import com.amar.fullstack.expanse_tracker_backend.dtos.*;
import com.amar.fullstack.expanse_tracker_backend.entity.NotificationType;
import com.amar.fullstack.expanse_tracker_backend.entity.User;
import com.amar.fullstack.expanse_tracker_backend.exception.InvalidCredentialsExceptions;
import com.amar.fullstack.expanse_tracker_backend.exception.ResourceNotFoundException;
import com.amar.fullstack.expanse_tracker_backend.exception.UserAllreadyExistsException;
import com.amar.fullstack.expanse_tracker_backend.notification.service.NotificationService;
import com.amar.fullstack.expanse_tracker_backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class AuthService {

    private final static Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;
    private NotificationService notificationService;
    private final OtpService otpService;
    private final StringRedisTemplate redisTemplate;
    public AuthService(UserRepository userRepo, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, NotificationService notificationService,OtpService otpService,StringRedisTemplate redisTemplate) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.notificationService=notificationService;
        this.otpService=otpService;
        this.redisTemplate=redisTemplate;
    }

    public void register(RegisterRequest request) {

        Optional<User> existUser = userRepo.findByEmail(request.getEmail());

        if (existUser.isPresent()) {
            throw new UserAllreadyExistsException("User already exists with this email");
        }

        String otp = otpService.generateOtp();
        otpService.saveOtp(request.getEmail(), otp);

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        String userData = request.getName() + "," +
                request.getEmail() + "," +
                encodedPassword + "," +
                request.getPhone();

        redisTemplate.opsForValue().set(
                "USER:" + request.getEmail(),
                userData, 5,
                TimeUnit.MINUTES
        );

        NotificationRequest notify = new NotificationRequest();
        notify.setEmail(request.getEmail());
        notify.setPhone(request.getPhone());

        // EMAIL (professional)
        notify.setMessage(
                "Hello,\n\n" +
                        "Thank you for registering with Expanse Tracker.\n\n" +
                        "Your One-Time Password (OTP) for email verification is:\n\n" +
                        otp + "\n\n" +
                        "This OTP is valid for 5 minutes. Please do not share it with anyone.\n\n" +
                        "----------------------------------\n" +
                        "This is a system-generated email. Please do not reply to this message."
        );

        notify.setSmsMessage(
                "Your OTP is " + otp + ". Valid for 5 minutes."
        );

        notify.setSubject("Verify your email");

        notify.setTypes(List.of(
                NotificationType.EMAIL,
                NotificationType.SMS
        ));

        notificationService.send(notify);

        logger.info("OTP sent to email: {} and phone: {}", request.getEmail(), request.getPhone());
    }
    public void verifyOtp(VerifyOtpRequest request){

        String storedOtp = otpService.getOtp(request.getEmail());

        if (storedOtp == null){
            throw new RuntimeException("OTP Expired");
        }
        if (!storedOtp.equals(request.getOtp())){
            throw new RuntimeException("Invalid Otp");
        }

        String data = redisTemplate.opsForValue()
                .get("USER:" + request.getEmail());

        if (data == null){
            throw new RuntimeException("User data not found");
        }

        String[] parts = data.split(",", 4); // safe split

        User user = new User();
        user.setName(parts[0]);
        user.setEmail(parts[1]);
        user.setPassword(parts[2]); // already encoded
        user.setPhone(parts[3]);

        userRepo.save(user);

        NotificationRequest notify = new NotificationRequest();
        notify.setEmail(request.getEmail());

        notify.setMessage(
                "Hello " + user.getName() + ",\n\n" +
                        "Your account has been successfully verified.\n\n" +
                        "You can now start using Expanse Tracker.\n\n" +
                        "Best regards,\n" +
                        "Expanse Tracker Team\n\n" +
                        "----------------------------------\n" +
                        "This is a system-generated email. Please do not reply to this message."
        );

        notify.setSubject("Welcome to Expanse Tracker 🎉");

        notify.setTypes(List.of(
                NotificationType.EMAIL
        ));

        notificationService.send(notify);

        otpService.deleteOtp(request.getEmail());
        redisTemplate.delete("USER:" + request.getEmail());
    }

    public AuthResponse login(AuthRequest request) {
        logger.info("Login attempt for email: {}", request.getEmail());
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    logger.warn("Login failed - email not found: {}", request.getEmail());
                    return new InvalidCredentialsExceptions("Invalid email or password");
                });
        boolean isMatch = passwordEncoder.matches(request.getPassword(), user.getPassword());
        logger.debug("Password match result for email {}: {}", request.getEmail(), isMatch);


        if (!isMatch) {
            logger.warn("Login failed - incorrect password for email: {}", request.getEmail());
            throw new InvalidCredentialsExceptions("Invalid email or password");
        }
        String token = jwtUtil.generateToken(user);
        logger.info("Login successful for email: {}", request.getEmail());
        return new AuthResponse(
                token,
                user.getName(),
                user.getEmail(),
                user.getRole().name());
    }

    public String forgotPassword(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Forgot password failed - email not found: {}", email);
                    return new ResourceNotFoundException("Invalid email");
                });
        String token = jwtUtil.generateResetToken(user);

        String resetLink="http://localhost:8080/api/auth/reset-password?token="+token;

        NotificationRequest notify = new NotificationRequest();
        notify.setEmail(user.getEmail());
        notify.setSubject("Reset Your Password");

        notify.setMessage(
                "Hello " + user.getName() + ",\n\n" +
                        "We received a request to reset your password.\n\n" +
                        "Click the link below to reset your password:\n\n" +
                        resetLink + "\n\n" +
                        "This link is valid for a limited time.\n\n" +
                        "If you did not request this, please ignore this email.\n\n" +
                        "----------------------------------\n" +
                        "This is a system-generated email. Please do not reply."
        );

        notify.setTypes(List.of(
                NotificationType.EMAIL
        ));

        notificationService.send(notify);

        logger.info("Reset password link sent to email: {}", email);
        return token;
    }

    public void resetPassword(ResetPasswordRequest request) {
        String email = jwtUtil.extractEmail(request.getToken());
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Reset password failed - email not found: {}", email);
                    return new InvalidCredentialsExceptions("Invalid token");
                });
        if (!jwtUtil.validateToken(request.getToken(), user)) {
            throw new InvalidCredentialsExceptions("Token expired or invalid");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepo.save(user);

        NotificationRequest notify=new NotificationRequest();

        notify.setEmail(user.getEmail());
        notify.setSubject("Password Reset Successfully");

        notify.setMessage(
                "Hello " + user.getName() + ",\n\n" +
                        "Your password has been successfully updated.\n\n" +
                        "If you did not perform this action, please contact support immediately.\n\n" +
                        "----------------------------------\n" +
                        "This is a system-generated email. Please do not reply."
        );
        notify.setTypes(List.of(
                NotificationType.EMAIL
        ));

        notificationService.send(notify);
        logger.info("Password reset successful for email: {}", email);

    }

}
