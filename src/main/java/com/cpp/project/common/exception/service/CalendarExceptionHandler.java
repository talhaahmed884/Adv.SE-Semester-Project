package com.cpp.project.common.exception.service;

import com.cpp.project.calendar.entity.CalendarException;
import com.cpp.project.common.exception.entity.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalendarExceptionHandler extends ExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(CalendarExceptionHandler.class);

    @Override
    protected boolean canHandle(Exception exception) {
        return exception instanceof CalendarException;
    }

    @Override
    protected void doHandle(Exception exception) {
        CalendarException calendarException = (CalendarException) exception;
        logger.error("Calendar exception occurred: Code={}, Message={}",
                calendarException.getCode(),
                calendarException.getMessage());
    }
}
