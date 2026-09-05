package com.amar.fullstack.expanse_tracker_backend.mapping;

import com.amar.fullstack.expanse_tracker_backend.dtos.UserResponseDto;
import com.amar.fullstack.expanse_tracker_backend.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public static UserResponseDto toDto(User user){
        return new UserResponseDto(
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getProfileImage()
        );
    }
}
