package com.cpp.project.user.service;

public interface PasswordHashingStrategy {
    String hash(String password);

    boolean verify(String password, String hash);
}
