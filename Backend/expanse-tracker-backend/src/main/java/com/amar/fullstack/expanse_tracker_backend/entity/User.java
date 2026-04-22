package com.amar.fullstack.expanse_tracker_backend.entity;
import com.amar.fullstack.expanse_tracker_backend.entity.Role;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String phone;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private String profileImage;

    // 🔐 OTP fields (SAFE: nullable)
    @Column(length = 6)
    private String otp;

    private Long otpExpiry;

    // ===== GETTERS & SETTERS =====
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getPhone() { return phone; }
    public Role getRole() { return role; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public String getProfileImage() { return profileImage; }
    public String getOtp() { return otp; }
    public Long getOtpExpiry() { return otpExpiry; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setRole(Role role) { this.role = role; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }
    public void setOtp(String otp) { this.otp = otp; }
    public void setOtpExpiry(Long otpExpiry) { this.otpExpiry = otpExpiry; }
}