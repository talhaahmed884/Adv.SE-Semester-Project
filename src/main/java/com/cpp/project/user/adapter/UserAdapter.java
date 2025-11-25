package com.cpp.project.user.adapter;

import com.cpp.project.user.dto.UserDTO;
import com.cpp.project.user.entity.User;

// Adapter Pattern for converting between Entity and DTO
public class UserAdapter {
    public static UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User toEntity(UserDTO dto) {
        if (dto == null) {
            return null;
        }

        return User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
    }
}
