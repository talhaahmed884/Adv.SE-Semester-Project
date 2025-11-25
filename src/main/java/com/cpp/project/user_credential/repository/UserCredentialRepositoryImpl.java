package com.cpp.project.user_credential.repository;

import com.cpp.project.user_credential.entity.UserCredential;
import com.cpp.project.user_credential.entity.UserCredentialErrorCode;
import com.cpp.project.user_credential.entity.UserCredentialException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional
public class UserCredentialRepositoryImpl implements UserCredentialRepository {
    private static final Logger logger = LoggerFactory.getLogger(UserCredentialRepositoryImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public UserCredential save(UserCredential credential) {
        try {
            if (credential.getUserId() == null || !entityManager.contains(credential)) {
                entityManager.persist(credential);
                logger.info("Credential created successfully for user id: {}", credential.getUserId());
                return credential;
            } else {
                UserCredential updated = entityManager.merge(credential);
                logger.info("Credential updated successfully for user id: {}", credential.getUserId());
                return updated;
            }
        } catch (PersistenceException e) {
            logger.error("Failed to save credential for user id: {}", credential.getUserId(), e);
            throw new UserCredentialException(UserCredentialErrorCode.CREDENTIAL_CREATION_FAILED, e, e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserCredential> findByUserId(UUID userId) {
        try {
            UserCredential credential = entityManager.find(UserCredential.class, userId);
            return Optional.ofNullable(credential);
        } catch (Exception e) {
            logger.error("Error finding credential by user id: {}", userId, e);
            return Optional.empty();
        }
    }

    @Override
    public void delete(UserCredential credential) {
        try {
            if (entityManager.contains(credential)) {
                entityManager.remove(credential);
            } else {
                entityManager.remove(entityManager.merge(credential));
            }
            logger.info("Credential deleted successfully for user id: {}", credential.getUserId());
        } catch (Exception e) {
            logger.error("Failed to delete credential for user id: {}", credential.getUserId(), e);
            throw new UserCredentialException(UserCredentialErrorCode.CREDENTIAL_UPDATE_FAILED, e, e.getMessage());
        }
    }

    @Override
    public void deleteByUserId(UUID userId) {
        try {
            findByUserId(userId).ifPresent(this::delete);
        } catch (Exception e) {
            logger.error("Failed to delete credential by user id: {}", userId, e);
            throw new UserCredentialException(UserCredentialErrorCode.CREDENTIAL_UPDATE_FAILED, e, userId.toString());
        }
    }
}
