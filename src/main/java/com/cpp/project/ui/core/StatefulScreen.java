package com.cpp.project.ui.core;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;

import java.io.IOException;

/**
 * Base class for screens that use the State pattern
 * Delegates rendering and input handling to current state
 */
public abstract class StatefulScreen extends UIScreen {
    protected ScreenState currentState;

    public StatefulScreen(Screen screen) {
        super(screen);
    }

    @Override
    protected void render() {
        if (currentState != null) {
            TextGraphics graphics = screen.newTextGraphics();
            currentState.render(graphics);
        }
    }

    @Override
    protected void handleInput() throws IOException {
        if (currentState != null) {
            KeyStroke keyStroke = screen.readInput();
            ScreenState newState = currentState.handleInput(keyStroke);

            // Only transition if state returned non-null and different state
            // null means mediator already handled the transition
            if (newState != null && newState != currentState) {
                transitionToState(newState);
            }
        }
    }

    /**
     * Transition to a new state
     */
    protected void transitionToState(ScreenState newState) {
        if (currentState != null) {
            currentState.onExit();
        }

        this.currentState = newState;

        if (currentState != null) {
            currentState.onEnter();
        }
    }
}
