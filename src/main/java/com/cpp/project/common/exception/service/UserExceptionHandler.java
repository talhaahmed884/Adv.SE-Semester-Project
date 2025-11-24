package com.cpp.project.common.exception.service;

import com.cpp.project.common.exception.entity.ExceptionHandler;
import com.cpp.project.user.entity.UserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserExceptionHandler extends ExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(UserExceptionHandler.class);

    @Override
    protected boolean canHandle(Exception exception) {
        return exception instanceof UserException;
    }

    @Override
    protected void doHandle(Exception exception) {
        UserException userException = (UserException) exception;
        logger.error("User exception occurred: Code={}, Message={}",
                userException.getCode(),
                userException.getMessage());
    }
}
