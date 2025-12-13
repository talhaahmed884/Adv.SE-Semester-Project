package com.cpp.project.user_credential.repository;

import com.cpp.project.common.repository.BaseJpaRepository;
import com.cpp.project.user_credential.entity.UserCredential;
import com.cpp.project.user_credential.entity.UserCredentialErrorCode;
import com.cpp.project.user_credential.entity.UserCredentialException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository implementation for UserCredential entity using JPA EntityManager
 * Extends BaseJpaRepository for common CRUD operations
 * <p>
 * Note: UserCredential uses userId as the primary key (not a separate id field)
 */
@Repository
@Transactional
public class UserCredentialRepositoryImpl extends BaseJpaRepository<UserCredential, UUID> implements UserCredentialRepository {
    private static final Logger logger = LoggerFactory.getLogger(UserCredentialRepositoryImpl.class);

    // ========== BaseJpaRepository Implementation ==========

    @Override
    protected Class<UserCredential> getEntityClass() {
        return UserCredential.class;
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected String getEntityName() {
        return "UserCredential";
    }

    @Override
    protected String getEntityIdentifier(UserCredential credential) {
        return credential.getUserId().toString();
    }

    @Override
    protected boolean isNew(UserCredential credential) {
        return credential.getUserId() == null;
    }

    @Override
    protected RuntimeException createNotFoundException(UUID userId) {
        return new UserCredentialException(UserCredentialErrorCode.CREDENTIAL_NOT_FOUND, "userId", userId);
    }

    @Override
    protected RuntimeException createNotFoundException(String fieldName, Object value) {
        return new UserCredentialException(UserCredentialErrorCode.CREDENTIAL_NOT_FOUND, fieldName, value);
    }

    @Override
    protected RuntimeException createSaveException(Exception cause) {
        return new UserCredentialException(UserCredentialErrorCode.CREDENTIAL_CREATION_FAILED, cause, cause.getMessage());
    }

    @Override
    protected RuntimeException createDeleteException(Object id) {
        return new UserCredentialException(UserCredentialErrorCode.CREDENTIAL_UPDATE_FAILED, id.toString());
    }

    // ========== Domain-Specific Methods ==========

    /**
     * Find credential by user ID (alias for findById since userId is the primary key)
     *
     * @param userId User ID
     * @return Optional containing credential if found
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<UserCredential> findByUserId(UUID userId) {
        return findById(userId);
    }

    /**
     * Delete credential by user ID (alias for deleteById since userId is the primary key)
     *
     * @param userId User ID
     */
    @Override
    public void deleteByUserId(UUID userId) {
        deleteById(userId);
    }
}
