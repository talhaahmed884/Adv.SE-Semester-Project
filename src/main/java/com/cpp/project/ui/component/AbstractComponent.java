package com.cpp.project.ui.component;

/**
 * Abstract base class for UI components
 * Provides common functionality
 */
public abstract class AbstractComponent implements UIComponent {
    protected boolean focused;
    protected int height;

    public AbstractComponent(int height) {
        this.height = height;
        this.focused = false;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public boolean isFocused() {
        return focused;
    }

    @Override
    public void setFocused(boolean focused) {
        this.focused = focused;
    }
}
