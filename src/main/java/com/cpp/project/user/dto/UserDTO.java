package com.cpp.project.user.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserDTO {
    private final UUID id;
    private final String name;
    private final String email;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    protected UserDTO(UserDTOBuilder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.email = builder.email;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
    }

    public static UserDTOBuilder builder() {
        return new UserDTOBuilder();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
