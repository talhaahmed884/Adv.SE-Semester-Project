package com.cpp.project.ui.core;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;

import java.io.IOException;

/**
 * State Pattern: Represents different states/modes of a screen
 * Each state handles its own rendering and input
 */
public interface ScreenState {
    /**
     * Render this state's UI
     */
    void render(TextGraphics graphics);

    /**
     * Handle input for this state
     *
     * @return new state if state transition occurs, or same state if no transition
     */
    ScreenState handleInput(KeyStroke keyStroke) throws IOException;

    /**
     * Called when entering this state
     */
    default void onEnter() {
        // Default: do nothing
    }

    /**
     * Called when exiting this state
     */
    default void onExit() {
        // Default: do nothing
    }

    /**
     * Get the state name for debugging
     */
    String getStateName();
}
