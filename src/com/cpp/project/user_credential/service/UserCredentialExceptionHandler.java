package com.cpp.project.user_credential.service;

import com.cpp.project.exception.entity.ExceptionHandler;
import com.cpp.project.user_credential.entity.UserCredentialException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserCredentialExceptionHandler extends ExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(UserCredentialExceptionHandler.class);

    @Override
    protected boolean canHandle(Exception exception) {
        return exception instanceof UserCredentialException;
    }

    @Override
    protected void doHandle(Exception exception) {
        UserCredentialException credException = (UserCredentialException) exception;
        logger.error("Credential exception occurred: Code={}, Message={}",
                credException.getCode(),
                credException.getMessage());
    }
}

