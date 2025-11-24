package com.cpp.project.authentication.dto;

import com.cpp.project.user.dto.UserDTO;

import java.time.LocalDateTime;

// Authentication response for signup/login
public class AuthResponseDTO {
    private final UserDTO user;
    private final String message;
    private final LocalDateTime timestamp;

    protected AuthResponseDTO(AuthResponseDTOBuilder builder) {
        this.user = builder.user;
        this.message = builder.message;
        this.timestamp = builder.timestamp;
    }

    public static AuthResponseDTOBuilder builder() {
        return new AuthResponseDTOBuilder();
    }

    // Getters
    public UserDTO getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
