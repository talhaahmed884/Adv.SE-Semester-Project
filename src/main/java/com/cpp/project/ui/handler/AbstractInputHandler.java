package com.cpp.project.ui.handler;

import com.googlecode.lanterna.input.KeyStroke;

/**
 * Base class for chain of responsibility input handlers
 */
public abstract class AbstractInputHandler implements InputHandler {
    protected InputHandler next;

    @Override
    public void setNext(InputHandler next) {
        this.next = next;
    }

    @Override
    public boolean handle(KeyStroke keyStroke) {
        if (canHandle(keyStroke)) {
            return doHandle(keyStroke);
        } else if (next != null) {
            return next.handle(keyStroke);
        }
        return false;
    }

    /**
     * Check if this handler can process the input
     */
    protected abstract boolean canHandle(KeyStroke keyStroke);

    /**
     * Actually handle the input
     */
    protected abstract boolean doHandle(KeyStroke keyStroke);
}
