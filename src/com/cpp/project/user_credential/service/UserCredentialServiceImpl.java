package com.cpp.project.user_credential.service;

import com.cpp.project.common.validation.entity.ValidationResult;
import com.cpp.project.common.validation.service.UserValidationService;
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
    private final UserValidationService validationService;

    public UserCredentialServiceImpl(UserRepository userRepository,
                                     UserCredentialRepository credentialRepository,
                                     UserValidationService validationService) {
        this.userRepository = userRepository;
        this.credentialRepository = credentialRepository;
        this.hashingStrategy = new SHA512HashingStrategy();
        this.validationService = validationService;
    }

    @Override
    public void setPassword(String email, String newPassword) {
        try {
            // Validate email using validation framework
            ValidationResult emailResult = validationService.validateEmail(email);

            if (!emailResult.isValid()) {
                throw new UserException(UserErrorCode.INVALID_EMAIL_FORMAT, email);
            }

            // Validate password STRENGTH using validation framework (for password changes)
            ValidationResult passwordResult = validationService.validatePasswordStrength(newPassword);

            if (!passwordResult.isValid()) {
                throw new UserCredentialException(UserCredentialErrorCode.PASSWORD_REQUIRED);
            }

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
            // Validate email using validation framework
            ValidationResult emailResult = validationService.validateEmail(email);

            if (!emailResult.isValid()) {
                throw new UserException(UserErrorCode.INVALID_EMAIL_FORMAT, email);
            }

            // Validate password INPUT only using validation framework (for verification)
            ValidationResult passwordResult = validationService.validatePasswordInput(password);

            if (!passwordResult.isValid()) {
                throw new UserCredentialException(UserCredentialErrorCode.PASSWORD_REQUIRED);
            }

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
}
