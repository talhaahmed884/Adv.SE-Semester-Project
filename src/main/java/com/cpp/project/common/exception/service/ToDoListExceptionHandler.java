package com.cpp.project.common.exception.service;

import com.cpp.project.common.exception.dto.ErrorResponseDTO;
import com.cpp.project.todolist.entity.ToDoListException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * Exception handler for ToDoList domain exceptions
 * Uses @RestControllerAdvice for global exception handling
 */
@RestControllerAdvice
public class ToDoListExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ToDoListExceptionHandler.class);

    @ExceptionHandler(ToDoListException.class)
    public ResponseEntity<ErrorResponseDTO> handleToDoListException(ToDoListException ex) {
        logger.error("ToDoList exception occurred: {}", ex.getMessage(), ex);

        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .code(ex.getCode())
                .message(ex.getMessage())
                .build();

        // Get HTTP status directly from error code
        HttpStatus status = ex.getErrorCode().getHttpStatus();

        return new ResponseEntity<>(errorResponse, status);
    }
}
