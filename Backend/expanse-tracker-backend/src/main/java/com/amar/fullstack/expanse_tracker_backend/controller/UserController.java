package com.amar.fullstack.expanse_tracker_backend.controller;

import com.amar.fullstack.expanse_tracker_backend.entity.User;
import com.amar.fullstack.expanse_tracker_backend.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService=userService;
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(Authentication auth){
        User user=(User) auth.getPrincipal();
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id){
        return ResponseEntity.ok(userService.getById(id));
    }

    @PostMapping("/upload-image")
    public ResponseEntity<?> uploadProfile(@RequestParam("file")MultipartFile file,
                                           Authentication auth){
        System.out.println(auth.getPrincipal());
        User user=(User) auth.getPrincipal();

        String email=user.getEmail();
        System.out.println("Email: "+email);
        userService.uploadProfileImage(email, file);
        return ResponseEntity.ok("Profile updated successfully");
    }

}
