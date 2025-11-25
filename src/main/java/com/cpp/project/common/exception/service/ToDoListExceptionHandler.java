package com.cpp.project.common.exception.service;

import com.cpp.project.common.exception.entity.ExceptionHandler;
import com.cpp.project.todolist.entity.ToDoListException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ToDoListExceptionHandler extends ExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ToDoListExceptionHandler.class);

    @Override
    protected boolean canHandle(Exception exception) {
        return exception instanceof ToDoListException;
    }

    @Override
    protected void doHandle(Exception exception) {
        ToDoListException todoListException = (ToDoListException) exception;
        logger.error("ToDoList exception occurred: Code={}, Message={}",
                todoListException.getCode(),
                todoListException.getMessage());
    }
}
