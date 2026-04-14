package com.amar.fullstack.expanse_tracker_backend.service;

import com.amar.fullstack.expanse_tracker_backend.config.JwtUtil;
import com.amar.fullstack.expanse_tracker_backend.dtos.AuthRequest;
import com.amar.fullstack.expanse_tracker_backend.dtos.AuthResponse;
import com.amar.fullstack.expanse_tracker_backend.dtos.RegisterRequest;
import com.amar.fullstack.expanse_tracker_backend.dtos.ResetPasswordRequest;
import com.amar.fullstack.expanse_tracker_backend.entity.User;
import com.amar.fullstack.expanse_tracker_backend.exception.InvalidCredentialsExceptions;
import com.amar.fullstack.expanse_tracker_backend.exception.ResourceNotFoundException;
import com.amar.fullstack.expanse_tracker_backend.exception.UserAllreadyExistsException;
import com.amar.fullstack.expanse_tracker_backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final static Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;

    public AuthService(UserRepository userRepo, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public void register(RegisterRequest request) {

        Optional<User> existUser = userRepo.findByEmail(request.getEmail());

        if (existUser.isPresent()) {
            throw new UserAllreadyExistsException("User already exists with this email");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());

        userRepo.save(user);
        logger.info("User registered successfully with email: {}", request.getEmail());
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
        logger.info("Forgot password token generated for email: {}", email);
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
        logger.info("Password reset successful for email: {}", email);

    }

}
