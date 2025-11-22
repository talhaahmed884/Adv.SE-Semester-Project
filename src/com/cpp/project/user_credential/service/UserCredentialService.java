package com.cpp.project.user_credential.service;

public interface UserCredentialService {
    void setPassword(String email, String newPassword);

    boolean verifyPassword(String email, String password);
}
