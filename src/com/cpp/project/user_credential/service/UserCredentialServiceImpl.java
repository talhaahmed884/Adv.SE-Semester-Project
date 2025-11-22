package com.cpp.project.user_credential.service;

import com.cpp.project.user.entity.User;
import com.cpp.project.user.entity.UserErrorCode;
import com.cpp.project.user.entity.UserException;
import com.cpp.project.user.repository.UserRepository;
import com.cpp.project.user.service.PasswordHashingStrategy;
import com.cpp.project.user.service.SHA512HashingStrategy;
import com.cpp.project.user_credential.entity.UserCredential;
import com.cpp.project.user_credential.entity.UserCredentialErrorCode;
import com.cpp.project.user_credential.entity.UserCredentialException;
import com.cpp.project.user_credential.repository.UserCredentialRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserCredentialServiceImpl implements UserCredentialService {
    private static final Logger logger = LoggerFactory.getLogger(UserCredentialServiceImpl.class);

    private final UserRepository userRepository;
    private final UserCredentialRepository credentialRepository;
    private final PasswordHashingStrategy hashingStrategy;

    public UserCredentialServiceImpl(UserRepository userRepository,
                                     UserCredentialRepository credentialRepository) {
        this.userRepository = userRepository;
        this.credentialRepository = credentialRepository;
        this.hashingStrategy = new SHA512HashingStrategy();
    }

    @Override
    public void setPassword(String email, String newPassword) {
        try {
            validateEmailNotEmpty(email);

            validatePasswordNotEmpty(newPassword);

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND, "email", email));

            UserCredential credential = credentialRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new UserCredentialException(
                            UserCredentialErrorCode.CREDENTIAL_NOT_FOUND, email));

            String newPasswordHash = hashingStrategy.hash(newPassword);
            credential.setPasswordHash(newPasswordHash);

            credentialRepository.save(credential);
            logger.info("Password updated successfully for user: {}", email);
        } catch (UserException | UserCredentialException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error setting password for user: {}", email, e);
            throw new UserCredentialException(UserCredentialErrorCode.CREDENTIAL_UPDATE_FAILED, e, e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verifyPassword(String email, String password) {
        try {
            validateEmailNotEmpty(email);

            validatePasswordNotEmpty(password);

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND, "email", email));

            UserCredential credential = credentialRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new UserCredentialException(
                            UserCredentialErrorCode.CREDENTIAL_NOT_FOUND, email));

            boolean isValid = hashingStrategy.verify(password, credential.getPasswordHash());
            logger.debug("Password verification for user {}: {}", email, isValid ? "success" : "failed");
            return isValid;
        } catch (UserException | UserCredentialException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error verifying password for user: {}", email, e);
            throw new UserCredentialException(UserCredentialErrorCode.PASSWORD_VERIFICATION_FAILED, e, e.getMessage());
        }
    }

    private void validateEmailNotEmpty(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new UserException(UserErrorCode.INVALID_EMAIL_FORMAT, "null or empty");
        }
    }

    private void validatePasswordNotEmpty(String password) {
        if (password == null || password.isEmpty()) {
            throw new UserCredentialException(UserCredentialErrorCode.PASSWORD_REQUIRED);
        }
    }
}
