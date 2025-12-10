package com.cpp.project.ui.component;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;

/**
 * Observer Pattern: Message display component
 * Observes and displays error/success messages
 */
public class MessagePanel extends AbstractComponent {
    private String errorMessage;
    private String successMessage;

    public MessagePanel() {
        super(1);
        this.errorMessage = "";
        this.successMessage = "";
    }

    @Override
    public void render(TextGraphics graphics, int x, int y) {
        if (!errorMessage.isEmpty()) {
            graphics.setForegroundColor(TextColor.ANSI.RED_BRIGHT);
            graphics.putString(x, y, "Error: " + errorMessage);
        } else if (!successMessage.isEmpty()) {
            graphics.setForegroundColor(TextColor.ANSI.GREEN_BRIGHT);
            graphics.putString(x, y, successMessage);
        }
    }

    @Override
    public boolean handleInput(KeyStroke keyStroke) {
        return false; // Message panel doesn't handle input
    }

    public void setError(String message) {
        this.errorMessage = message;
        this.successMessage = "";
    }

    public void setSuccess(String message) {
        this.successMessage = message;
        this.errorMessage = "";
    }

    public void clear() {
        this.errorMessage = "";
        this.successMessage = "";
    }
}
