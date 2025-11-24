package com.cpp.project.user.dto;

public class UpdateUserRequestDTO {
    private String name;
    private String email;

    public UpdateUserRequestDTO() {
    }

    public UpdateUserRequestDTO(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEmpty() {
        return (name == null || name.trim().isEmpty()) &&
                (email == null || email.trim().isEmpty());
    }
}
