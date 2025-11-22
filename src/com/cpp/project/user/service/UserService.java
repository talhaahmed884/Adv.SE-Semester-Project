package com.cpp.project.user.service;

import com.cpp.project.user.dto.LoginRequestDTO;
import com.cpp.project.user.dto.SignUpRequestDTO;
import com.cpp.project.user.dto.UserDTO;

import java.util.UUID;

// Facade Pattern for User operations
public interface UserService {
    UserDTO signUp(SignUpRequestDTO request);

    boolean login(LoginRequestDTO request);

    UserDTO getUserById(UUID id);

    UserDTO getUserByEmail(String email);

    void updateUser(UUID id, String name, String email);

    void deleteUser(UUID id);
}

