package com.cpp.project.ui.strategy;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;

/**
 * Renders centered titles
 */
public class TitleRenderingStrategy implements RenderingStrategy {
    private final String title;
    private final int screenWidth;

    public TitleRenderingStrategy(String title, TerminalSize terminalSize) {
        this.title = title;
        this.screenWidth = terminalSize.getColumns();
    }

    @Override
    public void render(TextGraphics graphics, int x, int y) {
        graphics.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
        int centerX = (screenWidth - title.length()) / 2;
        graphics.putString(centerX, y, title);
    }
}
