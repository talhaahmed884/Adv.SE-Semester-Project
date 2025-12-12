package com.cpp.project.common.controller.service;

import com.cpp.project.authentication.dto.AuthResponseDTO;
import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.common.controller.dto.ApiSuccessResponse;
import com.cpp.project.timer.service.TimerService;
import com.cpp.project.user.dto.LoginRequestDTO;
import com.cpp.project.user.dto.LogoutRequestDTO;
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
    private final TimerService timerService;

    public AuthController(AuthenticationService authenticationService, TimerService timerService) {
        this.authenticationService = authenticationService;
        this.timerService = timerService;
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

    /**
     * Logout endpoint - stops all active timers for user
     * POST /api/auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiSuccessResponse<String>> logout(@RequestBody LogoutRequestDTO request) {

        if (request.isEmpty()) {
            throw new AuthenticationException(
                    AuthenticationErrorCode.AUTHENTICATION_FAILED,
                    "Invalid logout request: User ID is required"
            );
        }

        // Stop all active timers for the user
        timerService.stopAllActiveTimersForUser(request.getUserId());

        ApiSuccessResponse<String> response = ApiSuccessResponse.<String>builder()
                .data("Logout successful")
                .message("All active timers stopped")
                .statusCode(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }
}
