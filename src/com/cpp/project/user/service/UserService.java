package com.cpp.project.user.service;

import com.cpp.project.user.dto.UserDTO;
import com.cpp.project.user.entity.User;

import java.util.UUID;

// Facade Pattern for User operations
public interface UserService {
    User createUserWithoutCredential(String name, String email);

    UserDTO getUserById(UUID id);

    UserDTO getUserByEmail(String email);

    void updateUser(UUID id, String name, String email);

    void deleteUser(UUID id);
}
