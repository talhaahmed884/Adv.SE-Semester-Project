package com.cpp.project.user_credential.repository;

import com.cpp.project.user_credential.entity.UserCredential;

import java.util.Optional;
import java.util.UUID;

public interface UserCredentialRepository {
    UserCredential save(UserCredential credential);

    Optional<UserCredential> findByUserId(UUID userId);

    void delete(UserCredential credential);

    void deleteByUserId(UUID userId);
}