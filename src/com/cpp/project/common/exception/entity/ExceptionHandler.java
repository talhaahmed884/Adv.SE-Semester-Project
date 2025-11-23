package com.cpp.project.common.exception.entity;

// Chain of Responsibility Pattern for exception handling
public abstract class ExceptionHandler {
    protected ExceptionHandler next;

    public ExceptionHandler setNext(ExceptionHandler handler) {
        this.next = handler;
        return handler;
    }

    public void handle(Exception exception) {
        if (canHandle(exception)) {
            doHandle(exception);
        } else if (next != null) {
            next.handle(exception);
        } else {
            handleDefault(exception);
        }
    }

    protected abstract boolean canHandle(Exception exception);

    protected abstract void doHandle(Exception exception);

    protected void handleDefault(Exception exception) {
        System.err.println("Unhandled exception: " + exception.getMessage());
        exception.printStackTrace();
    }
}

