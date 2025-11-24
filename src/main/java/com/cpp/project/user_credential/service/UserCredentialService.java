package com.cpp.project.user_credential.service;

import java.util.UUID;

public interface UserCredentialService {
    void createCredential(UUID userId, String password);

    void setPassword(String email, String newPassword);

    boolean verifyPassword(String email, String password);
}
