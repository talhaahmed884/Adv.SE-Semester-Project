package com.cpp.project.user.service;

import com.cpp.project.common.validation.entity.ValidationResult;
import com.cpp.project.common.validation.service.UserValidationService;
import com.cpp.project.user.adapter.UserAdapter;
import com.cpp.project.user.dto.LoginRequestDTO;
import com.cpp.project.user.dto.SignUpRequestDTO;
import com.cpp.project.user.dto.UserDTO;
import com.cpp.project.user.entity.*;
import com.cpp.project.user.repository.UserRepository;
import com.cpp.project.user_credential.entity.UserCredential;
import com.cpp.project.user_credential.entity.UserCredentialErrorCode;
import com.cpp.project.user_credential.entity.UserCredentialException;
import com.cpp.project.user_credential.repository.UserCredentialRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final UserCredentialRepository credentialRepository;
    private final PasswordHashingStrategy hashingStrategy;
    private final UserValidationService validationService;

    public UserServiceImpl(UserRepository userRepository,
                           UserCredentialRepository credentialRepository,
                           UserValidationService validationService) {
        this.userRepository = userRepository;
        this.credentialRepository = credentialRepository;
        this.hashingStrategy = new SHA512HashingStrategy();
        this.validationService = validationService;
    }

    @Override
    public UserDTO signUp(SignUpRequestDTO request) {
        try {
            // Validate request using validation framework
            ValidationResult validationResult = validationService.validateSignUpRequest(request);

            if (!validationResult.isValid()) {
                String firstError = validationResult.getFirstError();
                throw new UserException(UserErrorCode.INVALID_USER_DATA, firstError);
            }

            // Check if user already exists
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new UserException(UserErrorCode.USER_ALREADY_EXISTS, request.getEmail());
            }

            // Create user entity
            User user = User.builder()
                    .name(request.getName())
                    .email(request.getEmail())
                    .build();

            // Hash password and create credential
            String passwordHash = hashingStrategy.hash(request.getPassword());
            UserCredential credential = UserCredential.builder()
                    .passwordHash(passwordHash)
                    .algorithm("SHA-512")
                    .build();

            // Associate credential with user
            user.setCredential(credential);

            // Save user (cascade will save credential)
            User savedUser = userRepository.save(user);

            logger.info("User signed up successfully: {}", savedUser.getEmail());
            return UserAdapter.toDTO(savedUser);
        } catch (UserException | UserCredentialException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during sign up for email: {}", request.getEmail(), e);
            throw new UserException(UserErrorCode.USER_CREATION_FAILED, e, e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean login(LoginRequestDTO request) {
        try {
            // Validate request using validation framework
            ValidationResult validationResult = validationService.validateLoginRequest(request);

            if (!validationResult.isValid()) {
                throw new AuthenticationException(AuthenticationErrorCode.INVALID_CREDENTIALS);
            }

            // Find user by email
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new AuthenticationException(
                            AuthenticationErrorCode.INVALID_CREDENTIALS));

            // Get credential
            UserCredential credential = credentialRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new UserCredentialException(
                            UserCredentialErrorCode.CREDENTIAL_NOT_FOUND, user.getEmail()));

            // Verify password
            boolean isValid = hashingStrategy.verify(request.getPassword(), credential.getPasswordHash());

            if (isValid) {
                logger.info("User logged in successfully: {}", request.getEmail());
            } else {
                logger.warn("Failed login attempt for user: {}", request.getEmail());
            }

            return isValid;
        } catch (UserException | UserCredentialException | AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during login for email: {}", request.getEmail(), e);
            throw new AuthenticationException(AuthenticationErrorCode.AUTHENTICATION_FAILED, e, request.getEmail());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserById(UUID id) {
        try {
            if (id == null) {
                throw new UserException(UserErrorCode.INVALID_USER_DATA, "User ID cannot be null");
            }

            User user = userRepository.findById(id)
                    .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND, "id", id));

            logger.debug("Retrieved user by id: {}", id);
            return UserAdapter.toDTO(user);
        } catch (UserException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error retrieving user by id: {}", id, e);
            throw new UserException(UserErrorCode.USER_NOT_FOUND, e, "id", id);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserByEmail(String email) {
        try {
            // Validate email using validation framework
            ValidationResult validationResult = validationService.validateEmail(email);

            if (!validationResult.isValid()) {
                throw new UserException(UserErrorCode.INVALID_EMAIL_FORMAT, email);
            }

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND, "email", email));

            logger.debug("Retrieved user by email: {}", email);
            return UserAdapter.toDTO(user);
        } catch (UserException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error retrieving user by email: {}", email, e);
            throw new UserException(UserErrorCode.USER_NOT_FOUND, e, "email", email);
        }
    }

    @Override
    public void updateUser(UUID id, String name, String email) {
        try {
            if (id == null) {
                throw new UserException(UserErrorCode.INVALID_USER_DATA, "User ID cannot be null");
            }

            User user = userRepository.findById(id)
                    .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND, "id", id));

            boolean updated = false;

            if (name != null && !name.trim().isEmpty()) {
                // Validate name using validation framework
                ValidationResult nameResult = validationService.validateUserName(name);

                if (!nameResult.isValid()) {
                    throw new UserException(UserErrorCode.INVALID_NAME);
                }

                user.setName(name);
                updated = true;
            }

            if (email != null && !email.equals(user.getEmail())) {
                // Validate email using validation framework
                ValidationResult emailResult = validationService.validateEmail(email);

                if (!emailResult.isValid()) {
                    throw new UserException(UserErrorCode.INVALID_EMAIL_FORMAT, email);
                }

                if (userRepository.existsByEmail(email)) {
                    throw new UserException(UserErrorCode.EMAIL_ALREADY_IN_USE, email);
                }
                user.setEmail(email);
                updated = true;
            }

            if (updated) {
                userRepository.save(user);
                logger.info("User updated successfully: {}", id);
            } else {
                logger.debug("No changes to update for user: {}", id);
            }
        } catch (UserException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error updating user: {}", id, e);
            throw new UserException(UserErrorCode.USER_UPDATE_FAILED, e, e.getMessage());
        }
    }

    @Override
    public void deleteUser(UUID id) {
        try {
            if (id == null) {
                throw new UserException(UserErrorCode.INVALID_USER_DATA, "User ID cannot be null");
            }

            if (userRepository.findById(id).isEmpty()) {
                throw new UserException(UserErrorCode.USER_NOT_FOUND, "id", id);
            }

            userRepository.deleteById(id);
            logger.info("User deleted successfully: {}", id);
        } catch (UserException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error deleting user: {}", id, e);
            assert id != null;
            throw new UserException(UserErrorCode.USER_DELETION_FAILED, e, id.toString());
        }
    }
}
