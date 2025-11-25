package com.cpp.project.user_credential.entity;

import com.cpp.project.common.validation.entity.ValidationResult;
import com.cpp.project.common.validation.service.PasswordHashValidator;
import com.cpp.project.user.entity.User;
import jakarta.persistence.*;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "user_credentials")
public class UserCredential {
    @Id
    @Column(name = "user_id")
    private UUID userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "algorithm", nullable = false)
    private String algorithm = "SHA-512";

    protected UserCredential() {
    }

    protected UserCredential(UserCredentialBuilder builder) {
        this.passwordHash = builder.passwordHash;
        this.algorithm = builder.algorithm != null ? builder.algorithm : "SHA-512";
    }

    public static UserCredentialBuilder builder() {
        return new UserCredentialBuilder();
    }

    public UUID getUserId() {
        return userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            this.userId = user.getId();
        }
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        PasswordHashValidator validator = new PasswordHashValidator();
        ValidationResult result = validator.validate(passwordHash);
        if (!result.isValid()) {
            throw new UserCredentialException(UserCredentialErrorCode.PASSWORD_HASH_EMPTY);
        }
        this.passwordHash = passwordHash;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserCredential that)) return false;
        return Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
