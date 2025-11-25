package com.cpp.project.todolist.entity;

import com.cpp.project.common.exception.entity.BaseException;

/**
 * Exception class for ToDoList domain
 * Extends BaseException following project's exception hierarchy
 */
public class ToDoListException extends BaseException {
    public ToDoListException(ToDoListErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }

    public ToDoListException(ToDoListErrorCode errorCode, Throwable cause, Object... args) {
        super(errorCode, cause, args);
    }
}
