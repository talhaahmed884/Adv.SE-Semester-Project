package com.cpp.project.ui.component.menu;

/**
 * Represents a menu item with label and action
 * Used by SelectionList to display menu options
 */
public class MenuItem {
    private final String label;
    private final Runnable action;

    public MenuItem(String label, Runnable action) {
        this.label = label;
        this.action = action;
    }

    public String getLabel() {
        return label;
    }

    public void executeAction() {
        action.run();
    }

    public Runnable getAction() {
        return action;
    }
}
