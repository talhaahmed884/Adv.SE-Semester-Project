package com.cpp.project.ui.component;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;

/**
 * Component Pattern: Base interface for all UI components
 * Allows composition of complex UIs from smaller reusable components
 */
public interface UIComponent {
    /**
     * Render the component
     *
     * @param graphics graphics context
     * @param x        x position
     * @param y        y position
     */
    void render(TextGraphics graphics, int x, int y);

    /**
     * Handle input for this component
     *
     * @return true if input was consumed, false otherwise
     */
    boolean handleInput(KeyStroke keyStroke);

    /**
     * Get component height in lines
     */
    int getHeight();

    /**
     * Check if component is focused
     */
    boolean isFocused();

    /**
     * Set component focus
     */
    void setFocused(boolean focused);
}
