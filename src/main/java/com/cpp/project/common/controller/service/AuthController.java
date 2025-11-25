package com.cpp.project.common.controller.service;

import com.cpp.project.authentication.dto.AuthResponseDTO;
import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.common.controller.dto.ApiSuccessResponse;
import com.cpp.project.user.dto.LoginRequestDTO;
import com.cpp.project.user.dto.SignUpRequestDTO;
import com.cpp.project.user.dto.UserDTO;
import com.cpp.project.user.entity.AuthenticationErrorCode;
import com.cpp.project.user.entity.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiSuccessResponse<AuthResponseDTO>> signUp(@RequestBody SignUpRequestDTO request) {
        UserDTO user = authenticationService.signUp(request);

        AuthResponseDTO authResponse = AuthResponseDTO.builder()
                .user(user)
                .message("User registered successfully")
                .build();

        ApiSuccessResponse<AuthResponseDTO> response = ApiSuccessResponse.<AuthResponseDTO>builder()
                .data(authResponse)
                .message("Signup successful")
                .statusCode(HttpStatus.CREATED.value())
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiSuccessResponse<AuthResponseDTO>> login(@RequestBody LoginRequestDTO request) {
        boolean loginSuccessful = authenticationService.login(request);

        if (!loginSuccessful) {
            // This shouldn't happen as service throws exception on failure
            // But keeping for safety
            throw new AuthenticationException(AuthenticationErrorCode.AUTHENTICATION_FAILED, request.getEmail());
        }

        // Get user details after successful login
        UserDTO user = authenticationService.getUserByEmail(request.getEmail());

        AuthResponseDTO authResponse = AuthResponseDTO.builder()
                .user(user)
                .message("Login successful")
                .build();

        ApiSuccessResponse<AuthResponseDTO> response = ApiSuccessResponse.<AuthResponseDTO>builder()
                .data(authResponse)
                .message("Login successful")
                .statusCode(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }
}
