package com.cpp.project.user.service;

import com.cpp.project.user_credential.entity.UserCredentialErrorCode;
import com.cpp.project.user_credential.entity.UserCredentialException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class SHA512HashingStrategy implements PasswordHashingStrategy {
    private static final Logger logger = LoggerFactory.getLogger(SHA512HashingStrategy.class);
    private static final String ALGORITHM = "SHA-512";
    private static final int SALT_LENGTH = 16;

    @Override
    public String hash(String password) {
        try {
            if (password == null || password.isEmpty()) {
                throw new UserCredentialException(UserCredentialErrorCode.PASSWORD_REQUIRED);
            }

            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);

            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());

            // Combine salt and hash
            byte[] combined = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(hashedPassword, 0, combined, salt.length, hashedPassword.length);

            String result = Base64.getEncoder().encodeToString(combined);
            logger.debug("Password hashed successfully");
            return result;
        } catch (NoSuchAlgorithmException e) {
            logger.error("Hashing algorithm not found: {}", ALGORITHM, e);
            throw new UserCredentialException(UserCredentialErrorCode.PASSWORD_HASH_FAILED, e, ALGORITHM);
        }
    }

    @Override
    public boolean verify(String password, String storedHash) {
        try {
            if (password == null || password.isEmpty()) {
                throw new UserCredentialException(UserCredentialErrorCode.PASSWORD_REQUIRED);
            }

            if (storedHash == null || storedHash.isEmpty()) {
                throw new UserCredentialException(UserCredentialErrorCode.PASSWORD_HASH_EMPTY);
            }

            byte[] combined = Base64.getDecoder().decode(storedHash);

            // Extract salt
            byte[] salt = new byte[SALT_LENGTH];
            System.arraycopy(combined, 0, salt, 0, SALT_LENGTH);

            // Extract stored hash
            byte[] storedHashBytes = new byte[combined.length - SALT_LENGTH];
            System.arraycopy(combined, SALT_LENGTH, storedHashBytes, 0, storedHashBytes.length);

            // Hash the provided password with the extracted salt
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());

            // Compare
            boolean isValid = MessageDigest.isEqual(hashedPassword, storedHashBytes);
            logger.debug("Password verification: {}", isValid ? "success" : "failed");
            return isValid;
        } catch (NoSuchAlgorithmException e) {
            logger.error("Verification algorithm not found: {}", ALGORITHM, e);
            throw new UserCredentialException(UserCredentialErrorCode.PASSWORD_VERIFICATION_FAILED, e, ALGORITHM);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid hash format", e);
            throw new UserCredentialException(UserCredentialErrorCode.PASSWORD_VERIFICATION_FAILED, e, "Invalid hash format");
        }
    }
}
