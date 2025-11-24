package com.cpp.project.user.dto;

import java.util.UUID;

public class UserDTOBuilder {
    protected UUID id;
    protected String name;
    protected String email;

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

    public UserDTO build() {
        return new UserDTO(this);
    }
}
