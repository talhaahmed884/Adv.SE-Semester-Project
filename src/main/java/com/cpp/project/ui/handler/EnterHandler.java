package com.cpp.project.ui.handler;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

/**
 * Handles ENTER key press
 */
public class EnterHandler extends AbstractInputHandler {
    private final Runnable enterAction;

    public EnterHandler(Runnable enterAction) {
        this.enterAction = enterAction;
    }

    @Override
    protected boolean canHandle(KeyStroke keyStroke) {
        return keyStroke.getKeyType() == KeyType.Enter;
    }

    @Override
    protected boolean doHandle(KeyStroke keyStroke) {
        enterAction.run();
        return true;
    }
}
