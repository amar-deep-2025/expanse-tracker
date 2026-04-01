package com.amar.fullstack.expanse_tracker_backend.service;

import com.amar.fullstack.expanse_tracker_backend.dtos.ExpanseResponseDto;
import com.amar.fullstack.expanse_tracker_backend.entity.Expanse;
import com.amar.fullstack.expanse_tracker_backend.entity.User;
import com.amar.fullstack.expanse_tracker_backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepo;

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public Optional<User> getCurrentUser(String email){
        return userRepo.findByEmail(email);
    }

    public User getById(Long id){
        return userRepo.findById(id).orElseThrow(()->new RuntimeException("User not found with id: "+id));
    }


    public void uploadProfileImage(String email, MultipartFile file) {
        User user = userRepo.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        String uploadDir = System.getProperty("user.dir") + "/uploads/";
        File folder = new File(uploadDir);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String filePath = uploadDir + fileName;
        try {
            file.transferTo(new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
            // throw new RuntimeException("File upload failed: " + e.getMessage());
        }

        user.setProfileImage(fileName); // only file name store karo
        userRepo.save(user);
    }
}


