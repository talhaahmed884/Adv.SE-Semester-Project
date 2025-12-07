package com.cpp.project.ui.core;

import com.googlecode.lanterna.screen.Screen;

import java.io.IOException;

/**
 * Template Method Pattern: Base class for all UI screens
 * Defines the common lifecycle and behavior for all screens
 */
public abstract class UIScreen {
    protected final Screen screen;
    protected boolean running;

    public UIScreen(Screen screen) {
        this.screen = screen;
        this.running = true;
    }

    /**
     * Template method defining the screen display lifecycle
     */
    public final void display() throws IOException {
        onEnter();

        while (running) {
            screen.clear();
            render();
            screen.refresh();
            handleInput();
        }

        onExit();
    }

    /**
     * Called when screen is entered
     */
    protected void onEnter() {
        // Default: do nothing
    }

    /**
     * Render the screen content
     */
    protected abstract void render();

    /**
     * Handle user input
     */
    protected abstract void handleInput() throws IOException;

    /**
     * Called when screen is exited
     */
    protected void onExit() {
        // Default: do nothing
    }

    /**
     * Close this screen
     */
    protected void close() {
        this.running = false;
    }
}
