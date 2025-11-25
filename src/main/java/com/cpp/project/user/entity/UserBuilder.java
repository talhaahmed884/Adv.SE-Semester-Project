package com.cpp.project.user.entity;

import com.cpp.project.common.validation.entity.ValidationResult;
import com.cpp.project.common.validation.service.EmailValidator;
import com.cpp.project.common.validation.service.UserNameValidator;

// Builder Pattern with validation
public class UserBuilder {
    private final UserNameValidator nameValidator = new UserNameValidator();
    private final EmailValidator emailValidator = new EmailValidator();
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
        ValidationResult nameResult = nameValidator.validate(name);
        if (!nameResult.isValid()) {
            throw new UserException(UserErrorCode.INVALID_NAME);
        }

        ValidationResult emailResult = emailValidator.validate(email);
        if (!emailResult.isValid()) {
            throw new UserException(UserErrorCode.INVALID_EMAIL_FORMAT, email);
        }
    }
}
