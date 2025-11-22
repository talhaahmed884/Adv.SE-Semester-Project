package com.cpp.project.user.entity;

public class UserBuilder {
    protected String name;
    protected String email;

    public UserBuilder name(String name) {
        this.name = name;
        return this;
    }

    public UserBuilder email(String email) {
        this.email = email;
        return this;
    }

    public User build() {
        validate();
        return new User(this);
    }

    private void validate() {
        if (name == null || name.trim().isEmpty()) {
            throw new UserException(UserErrorCode.INVALID_NAME);
        }
        if (email == null || !isValidEmail(email)) {
            throw new UserException(UserErrorCode.INVALID_EMAIL_FORMAT, email);
        }
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}
