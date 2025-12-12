package com.cpp.project.common.exception.service;

import com.cpp.project.common.exception.entity.ExceptionHandler;
import com.cpp.project.timer.entity.TimerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimerExceptionHandler extends ExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(TimerExceptionHandler.class);

    @Override
    protected boolean canHandle(Exception exception) {
        return exception instanceof TimerException;
    }

    @Override
    protected void doHandle(Exception exception) {
        TimerException timerException = (TimerException) exception;
        logger.error("Timer exception occurred: Code={}, Message={}",
                timerException.getCode(),
                timerException.getMessage());
    }
}
