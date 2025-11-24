package com.cpp.project.common.exception.service;

import com.cpp.project.common.exception.dto.ErrorResponseDTO;
import com.cpp.project.common.exception.entity.BaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handle all custom exceptions (UserException, AuthenticationException, etc.)
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponseDTO> handleBaseException(
            BaseException ex,
            WebRequest request) {

        logger.error("BaseException occurred: code={}, message={}",
                ex.getCode(), ex.getMessage(), ex);

        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .code(ex.getCode())
                .message(ex.getMessage())
                .statusCode(ex.getErrorCode().getHttpStatus().value())
                .path(getRequestPath(request))
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, ex.getErrorCode().getHttpStatus());
    }

    /**
     * Handle Bean Validation errors (@Valid annotation)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationException(
            MethodArgumentNotValidException ex,
            WebRequest request) {

        logger.error("Validation exception occurred", ex);

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .code("VALIDATION_ERROR")
                .message("Validation failed for one or more fields")
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .path(getRequestPath(request))
                .fieldErrors(fieldErrors)
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle malformed JSON in request body
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            WebRequest request) {

        logger.error("Malformed JSON request", ex);

        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .code("MALFORMED_JSON")
                .message("Malformed JSON request body")
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .path(getRequestPath(request))
                .addDetail(ex.getMostSpecificCause().getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle wrong HTTP method (e.g., GET instead of POST)
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            WebRequest request) {

        logger.error("HTTP method not supported", ex);

        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .code("METHOD_NOT_ALLOWED")
                .message("HTTP method not supported for this endpoint")
                .statusCode(HttpStatus.METHOD_NOT_ALLOWED.value())
                .path(getRequestPath(request))
                .addDetail("Supported methods: " + String.join(", ", ex.getSupportedMethods()))
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * Handle missing request parameters
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponseDTO> handleMissingParams(
            MissingServletRequestParameterException ex,
            WebRequest request) {

        logger.error("Missing request parameter", ex);

        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .code("MISSING_PARAMETER")
                .message("Required request parameter is missing")
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .path(getRequestPath(request))
                .addDetail("Parameter '" + ex.getParameterName() + "' is required")
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle all other unexpected exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGlobalException(
            Exception ex,
            WebRequest request) {

        logger.error("Unexpected exception occurred", ex);

        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .code("INTERNAL_ERROR")
                .message("An unexpected error occurred")
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .path(getRequestPath(request))
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Extract request path from WebRequest
     */
    private String getRequestPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}
