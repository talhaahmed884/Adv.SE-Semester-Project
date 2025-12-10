package com.cpp.project.ui.handler;

import com.googlecode.lanterna.input.KeyStroke;

/**
 * Chain of Responsibility Pattern: Input handler interface
 * Each handler in the chain can process or pass on input
 */
public interface InputHandler {
    /**
     * Handle the keystroke
     *
     * @return true if input was handled, false to pass to next handler
     */
    boolean handle(KeyStroke keyStroke);

    /**
     * Set the next handler in the chain
     */
    void setNext(InputHandler next);
}
