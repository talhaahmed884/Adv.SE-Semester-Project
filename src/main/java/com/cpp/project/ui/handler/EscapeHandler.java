package com.cpp.project.ui.handler;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

/**
 * Handles ESC key press
 */
public class EscapeHandler extends AbstractInputHandler {
    private final Runnable escapeAction;

    public EscapeHandler(Runnable escapeAction) {
        this.escapeAction = escapeAction;
    }

    @Override
    protected boolean canHandle(KeyStroke keyStroke) {
        return keyStroke.getKeyType() == KeyType.Escape;
    }

    @Override
    protected boolean doHandle(KeyStroke keyStroke) {
        escapeAction.run();
        return true;
    }
}
