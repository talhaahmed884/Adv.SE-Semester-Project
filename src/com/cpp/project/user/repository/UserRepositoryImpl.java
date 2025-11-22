package com.cpp.project.user.repository;

import com.cpp.project.user.entity.User;
import com.cpp.project.user.entity.UserErrorCode;
import com.cpp.project.user.entity.UserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional
public class UserRepositoryImpl implements UserRepository {
    private static final Logger logger = LoggerFactory.getLogger(UserRepositoryImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public User save(User user) {
        try {
            if (user.getId() == null || !entityManager.contains(user)) {
                entityManager.persist(user);
                logger.info("User created successfully with id: {}", user.getId());
                return user;
            } else {
                User updated = entityManager.merge(user);
                logger.info("User updated successfully with id: {}", user.getId());
                return updated;
            }
        } catch (PersistenceException e) {
            logger.error("Failed to save user with email: {}", user.getEmail(), e);
            throw new UserException(UserErrorCode.USER_CREATION_FAILED, e, e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(UUID id) {
        try {
            User user = entityManager.find(User.class, id);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            logger.error("Error finding user by id: {}", id, e);
            throw new UserException(UserErrorCode.USER_NOT_FOUND, e, "id", id);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        try {
            TypedQuery<User> query = entityManager.createQuery(
                    "SELECT u FROM User u WHERE u.email = :email", User.class);
            query.setParameter("email", email);

            return Optional.ofNullable(query.getSingleResult());
        } catch (Exception e) {
            logger.debug("User not found with email: {}", email);
            return Optional.empty();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        try {
            TypedQuery<User> query = entityManager.createQuery(
                    "SELECT u FROM User u", User.class);
            return query.getResultList();
        } catch (Exception e) {
            logger.error("Error retrieving all users", e);
            throw new UserException(UserErrorCode.USER_NOT_FOUND, e, "all", "users");
        }
    }

    @Override
    public void delete(User user) {
        try {
            if (entityManager.contains(user)) {
                entityManager.remove(user);
            } else {
                entityManager.remove(entityManager.merge(user));
            }
            logger.info("User deleted successfully with id: {}", user.getId());
        } catch (Exception e) {
            logger.error("Failed to delete user with id: {}", user.getId(), e);
            throw new UserException(UserErrorCode.USER_DELETION_FAILED, e, e.getMessage());
        }
    }

    @Override
    public void deleteById(UUID id) {
        try {
            findById(id).ifPresent(this::delete);
        } catch (Exception e) {
            logger.error("Failed to delete user by id: {}", id, e);
            throw new UserException(UserErrorCode.USER_DELETION_FAILED, e, id.toString());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                    "SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class);
            query.setParameter("email", email);
            return query.getSingleResult() > 0;
        } catch (Exception e) {
            logger.error("Error checking email existence: {}", email, e);
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                    "SELECT COUNT(u) FROM User u", Long.class);
            return query.getSingleResult();
        } catch (Exception e) {
            logger.error("Error counting users", e);
            return 0;
        }
    }
}
