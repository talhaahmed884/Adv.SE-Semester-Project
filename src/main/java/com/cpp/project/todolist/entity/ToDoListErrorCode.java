package com.cpp.project.todolist.entity;

import com.cpp.project.common.exception.entity.ErrorCode;
import org.springframework.http.HttpStatus;

/**
 * Error codes for ToDoList domain
 */
public enum ToDoListErrorCode implements ErrorCode {
    // ToDoList errors
    TODO_LIST_NOT_FOUND("TODO_001", "Todo list not found with %s: %s", HttpStatus.NOT_FOUND),
    INVALID_TODO_LIST_NAME("TODO_002", "Invalid todo list name: %s", HttpStatus.BAD_REQUEST),
    TODO_LIST_CREATION_FAILED("TODO_003", "Failed to create todo list: %s", HttpStatus.INTERNAL_SERVER_ERROR),
    TODO_LIST_UPDATE_FAILED("TODO_004", "Failed to update todo list: %s", HttpStatus.INTERNAL_SERVER_ERROR),
    TODO_LIST_DELETION_FAILED("TODO_005", "Failed to delete todo list: %s", HttpStatus.INTERNAL_SERVER_ERROR),

    // ToDoList Task errors
    TASK_NOT_FOUND("TODO_TASK_001", "Task not found with id: %s", HttpStatus.NOT_FOUND),
    INVALID_TASK_DESCRIPTION("TODO_TASK_002", "Invalid task description: %s", HttpStatus.BAD_REQUEST),
    TASK_CREATION_FAILED("TODO_TASK_003", "Failed to create task: %s", HttpStatus.INTERNAL_SERVER_ERROR),
    TASK_UPDATE_FAILED("TODO_TASK_004", "Failed to update task: %s", HttpStatus.INTERNAL_SERVER_ERROR),

    // Validation errors
    INVALID_TODO_LIST_DATA("TODO_VAL_001", "Invalid todo list data: %s", HttpStatus.BAD_REQUEST),
    INVALID_TASK_DATA("TODO_VAL_002", "Invalid task data: %s", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String messageTemplate;
    private final HttpStatus httpStatus;

    ToDoListErrorCode(String code, String messageTemplate, HttpStatus httpStatus) {
        this.code = code;
        this.messageTemplate = messageTemplate;
        this.httpStatus = httpStatus;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessageTemplate() {
        return messageTemplate;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
