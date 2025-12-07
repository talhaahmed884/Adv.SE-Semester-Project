package com.cpp.project.ui.core;

/**
 * Mediator interface for screen-state communication
 * States use this to interact with the screen without tight coupling
 * <p>
 * Design Patterns:
 * - Mediator Pattern: Defines the contract for state-screen communication
 * - Facade Pattern: Provides simplified interface for states
 */
public interface ScreenMediator {
    /**
     * Transition to a new state
     *
     * @param newState The state to transition to
     */
    void transitionTo(ScreenState newState);

    /**
     * Close the current screen
     */
    void closeScreen();
}
