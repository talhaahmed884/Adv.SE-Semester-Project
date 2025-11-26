package com.cpp.project.user.service;

import com.cpp.project.common.sanitization.service.DataSanitizationService;
import com.cpp.project.common.validation.entity.ValidationResult;
import com.cpp.project.common.validation.service.UserValidationService;
import com.cpp.project.user.adapter.UserAdapter;
import com.cpp.project.user.dto.UserDTO;
import com.cpp.project.user.entity.User;
import com.cpp.project.user.entity.UserErrorCode;
import com.cpp.project.user.entity.UserException;
import com.cpp.project.user.repository.UserRepository;
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
    private final UserValidationService validationService;
    private final DataSanitizationService dataSanitizationService;

    public UserServiceImpl(UserRepository userRepository, UserValidationService validationService, DataSanitizationService dataSanitizationService) {
        this.userRepository = userRepository;
        this.validationService = validationService;
        this.dataSanitizationService = dataSanitizationService;
    }

    @Override
    public User createUserWithoutCredential(String name, String email) {
        try {
            // Validate name using validation framework
            ValidationResult nameResult = validationService.validateUserName(name);

            if (!nameResult.isValid()) {
                throw new UserException(UserErrorCode.INVALID_NAME);
            }

            // Validate email using validation framework
            ValidationResult emailResult = validationService.validateEmail(email);

            if (!emailResult.isValid()) {
                throw new UserException(UserErrorCode.INVALID_EMAIL_FORMAT, email);
            }

            // Check if user already exists
            if (userRepository.existsByEmail(email)) {
                throw new UserException(UserErrorCode.USER_ALREADY_EXISTS, email);
            }

            // Create user entity without credential
            User user = User.builder()
                    .name(name)
                    .email(email)
                    .build();

            // Save user
            User savedUser = userRepository.save(user);

            logger.info("User created successfully without credential: {}", savedUser.getEmail());
            return savedUser;
        } catch (UserException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during user creation for email: {}", email, e);
            throw new UserException(UserErrorCode.USER_CREATION_FAILED, e, e.getMessage());
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

            email = dataSanitizationService.sanitizeEmail(email);
            name = dataSanitizationService.sanitizeName(name);

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
