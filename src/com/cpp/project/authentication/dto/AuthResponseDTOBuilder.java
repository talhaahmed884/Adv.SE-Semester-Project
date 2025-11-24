package com.cpp.project.authentication.dto;

import com.cpp.project.user.dto.UserDTO;

import java.time.LocalDateTime;

public class AuthResponseDTOBuilder {
    protected UserDTO user;
    protected String message;
    protected LocalDateTime timestamp = LocalDateTime.now();

    public AuthResponseDTOBuilder user(UserDTO user) {
        this.user = user;
        return this;
    }

    public AuthResponseDTOBuilder message(String message) {
        this.message = message;
        return this;
    }

    public AuthResponseDTOBuilder timestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public AuthResponseDTO build() {
        return new AuthResponseDTO(this);
    }
}
