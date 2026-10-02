package com.amar.fullstack.expanse_tracker_backend.config;

import com.amar.fullstack.expanse_tracker_backend.dtos.ErrorResponseDto;
import com.amar.fullstack.expanse_tracker_backend.security.JwtFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Profile("!test")
@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Autowired
        private JwtFilter jwtFilter;

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

                http
                                .csrf(csrf -> csrf.disable())
                                .cors(cors -> {
                                })
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/api/auth/**", "/error","/api/auth/verify-otp", "/api/ai/test", "/uploads/**").permitAll()
                                                .requestMatchers("api/users","/api/admin/**").hasRole("ADMIN")
                                                .anyRequest().authenticated())
                        .exceptionHandling(exception->exception
                                .accessDeniedHandler((request,response,ex)->{
                                        response.setStatus(HttpStatus.FORBIDDEN.value());
                                        response.setContentType("application/json");

                                        ErrorResponseDto error=new ErrorResponseDto(
                                                "You do not have permission to access this resource",
                                                HttpStatus.FORBIDDEN.value(),
                                                request.getRequestURI()
                                        );
                                        new ObjectMapper().writeValue(response.getWriter(),error);
                                }))
                                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}