package com.cpp.project.user_credential.entity;

public class UserCredentialBuilder {
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
        if (passwordHash == null || passwordHash.trim().isEmpty()) {
            throw new UserCredentialException(UserCredentialErrorCode.PASSWORD_HASH_EMPTY);
        }
    }
}