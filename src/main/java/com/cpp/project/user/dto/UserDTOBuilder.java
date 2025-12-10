package com.cpp.project.user.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserDTOBuilder {
    protected UUID id;
    protected String name;
    protected String email;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;

    public UserDTOBuilder id(UUID id) {
        this.id = id;
        return this;
    }

    public UserDTOBuilder name(String name) {
        this.name = name;
        return this;
    }

    public UserDTOBuilder email(String email) {
        this.email = email;
        return this;
    }

    public UserDTOBuilder createdAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public UserDTOBuilder updatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public UserDTO build() {
        return new UserDTO(this);
    }
}
