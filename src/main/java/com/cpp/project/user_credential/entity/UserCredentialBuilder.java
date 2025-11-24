package com.cpp.project.user_credential.entity;

import com.cpp.project.common.validation.entity.ValidationResult;
import com.cpp.project.common.validation.service.PasswordHashValidator;

// Builder Pattern with validation
public class UserCredentialBuilder {
    private final PasswordHashValidator hashValidator = new PasswordHashValidator();
    protected String passwordHash;
    protected String algorithm = "SHA-512";

    public UserCredentialBuilder passwordHash(String passwordHash) {
        this.passwordHash = passwordHash;
        return this;
    }

    public UserCredentialBuilder algorithm(String algorithm) {
        this.algorithm = algorithm;
        return this;
    }

    public UserCredential build() {
        validate();
        return new UserCredential(this);
    }

    private void validate() {
        ValidationResult result = hashValidator.validate(passwordHash);
        if (!result.isValid()) {
            throw new UserCredentialException(UserCredentialErrorCode.PASSWORD_HASH_EMPTY);
        }
    }
}
