package com.cpp.project.user.repository;

import com.cpp.project.common.repository.BaseJpaRepository;
import com.cpp.project.user.entity.User;
import com.cpp.project.user.entity.UserErrorCode;
import com.cpp.project.user.entity.UserException;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository implementation for User entity using JPA EntityManager
 * Extends BaseJpaRepository for common CRUD operations
 */
@Repository
@Transactional
public class UserRepositoryImpl extends BaseJpaRepository<User, UUID> implements UserRepository {
    private static final Logger logger = LoggerFactory.getLogger(UserRepositoryImpl.class);

    // ========== BaseJpaRepository Implementation ==========

    @Override
    protected Class<User> getEntityClass() {
        return User.class;
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected String getEntityName() {
        return "User";
    }

    @Override
    protected String getEntityIdentifier(User user) {
        return user.getEmail();
    }

    @Override
    protected boolean isNew(User user) {
        return user.getId() == null;
    }

    @Override
    protected RuntimeException createNotFoundException(UUID id) {
        return new UserException(UserErrorCode.USER_NOT_FOUND, "id", id);
    }

    @Override
    protected RuntimeException createNotFoundException(String fieldName, Object value) {
        return new UserException(UserErrorCode.USER_NOT_FOUND, fieldName, value);
    }

    @Override
    protected RuntimeException createSaveException(Exception cause) {
        return new UserException(UserErrorCode.USER_CREATION_FAILED, cause, cause.getMessage());
    }

    @Override
    protected RuntimeException createDeleteException(Object id) {
        return new UserException(UserErrorCode.USER_DELETION_FAILED, id.toString());
    }

    // ========== Domain-Specific Methods ==========

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
}
