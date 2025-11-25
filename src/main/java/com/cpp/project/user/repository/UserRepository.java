package com.cpp.project.user.repository;

import com.cpp.project.user.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);

    Optional<User> findById(UUID id);

    Optional<User> findByEmail(String email);

    List<User> findAll();

    void delete(User user);

    void deleteById(UUID id);

    boolean existsByEmail(String email);

    long count();
}
