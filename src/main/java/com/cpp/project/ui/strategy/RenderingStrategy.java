package com.cpp.project.ui.strategy;

import com.googlecode.lanterna.graphics.TextGraphics;

/**
 * Strategy Pattern: Interface for rendering strategies
 * Allows different rendering approaches to be swapped
 */
public interface RenderingStrategy {
    /**
     * Render content using this strategy
     */
    void render(TextGraphics graphics, int x, int y);
}
