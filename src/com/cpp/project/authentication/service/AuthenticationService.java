package com.cpp.project.authentication.service;

import com.cpp.project.user.dto.LoginRequestDTO;
import com.cpp.project.user.dto.SignUpRequestDTO;
import com.cpp.project.user.dto.UserDTO;

public interface AuthenticationService {
    UserDTO signUp(SignUpRequestDTO request);

    boolean login(LoginRequestDTO request);
}
