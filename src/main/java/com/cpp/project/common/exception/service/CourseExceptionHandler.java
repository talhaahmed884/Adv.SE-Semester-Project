package com.cpp.project.common.exception.service;

import com.cpp.project.common.exception.entity.ExceptionHandler;
import com.cpp.project.course.entity.CourseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CourseExceptionHandler extends ExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(CourseExceptionHandler.class);

    @Override
    protected boolean canHandle(Exception exception) {
        return exception instanceof CourseException;
    }

    @Override
    protected void doHandle(Exception exception) {
        CourseException courseException = (CourseException) exception;
        logger.error("Course exception occurred: Code={}, Message={}",
                courseException.getCode(),
                courseException.getMessage());
    }
}
