package com.cpp.project.user.service;

import com.cpp.project.exception.entity.ExceptionHandler;
import com.cpp.project.user.entity.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthenticationExceptionHandler extends ExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationExceptionHandler.class);

    @Override
    protected boolean canHandle(Exception exception) {
        return exception instanceof AuthenticationException;
    }

    @Override
    protected void doHandle(Exception exception) {
        AuthenticationException authException = (AuthenticationException) exception;
        logger.error("Authentication exception occurred: Code={}, Message={}",
                authException.getCode(),
                authException.getMessage());
    }
}
