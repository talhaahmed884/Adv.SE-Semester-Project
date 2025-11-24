package com.cpp.project.user.dto;

import java.util.UUID;

public class UserDTO {
    private final UUID id;
    private final String name;
    private final String email;

    protected UserDTO(UserDTOBuilder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.email = builder.email;
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
}
