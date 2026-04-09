package com.amar.fullstack.expanse_tracker_backend.config;

import com.amar.fullstack.expanse_tracker_backend.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // 🔐 Must be at least 32 characters
    @Value("${jwt.secret:testsecretkey1234567890testsecretkey1234567890}")
    private String SECRET;

    @Value("${jwt.expiration:3600000}")
    private long EXPIRATION;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // 🔥 Generate Token
    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole().name()) // FIXED
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))// 30 minutes
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 🔍 Extract Email
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    // 🔍 Extract Role
    public String extractRole(String token) {
        return (String) extractClaims(token).get("role");
    }

    // 🔍 Validate Token
    public boolean validateToken(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (Exception e) {
            System.out.println("Invalid JWT: " + e.getMessage());
            return false;
        }
    }

    // 🔍 Extract Claims
    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}