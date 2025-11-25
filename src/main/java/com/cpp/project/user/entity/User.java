package com.cpp.project.user.entity;

import com.cpp.project.common.validation.entity.ValidationResult;
import com.cpp.project.common.validation.service.EmailValidator;
import com.cpp.project.common.validation.service.UserNameValidator;
import com.cpp.project.user_credential.entity.UserCredential;
import jakarta.persistence.*;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserCredential credential;

    protected User() {
        this.id = UUID.randomUUID();
    }

    protected User(UserBuilder builder) {
        this.id = UUID.randomUUID();
        this.name = builder.name;
        this.email = builder.email;
    }

    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        UserNameValidator validator = new UserNameValidator();
        ValidationResult result = validator.validate(name);
        if (!result.isValid()) {
            throw new UserException(UserErrorCode.INVALID_NAME);
        }
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        EmailValidator validator = new EmailValidator();
        ValidationResult result = validator.validate(email);
        if (!result.isValid()) {
            throw new UserException(UserErrorCode.INVALID_EMAIL_FORMAT, email);
        }
        this.email = email;
    }

    public UserCredential getCredential() {
        return credential;
    }

    public void setCredential(UserCredential credential) {
        this.credential = credential;
        if (credential != null) {
            credential.setUser(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
