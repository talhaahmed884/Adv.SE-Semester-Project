package com.cpp.project.ui.component;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Component Pattern: Reusable selection list component
 * Generic list that can display any type of item
 */
public class SelectionList<T> extends AbstractComponent {
    private final String title;
    private final Function<T, String> displayFunction;
    private List<T> items;
    private int selectedIndex;

    public SelectionList(String title, Function<T, String> displayFunction) {
        super(10); // Default height
        this.title = title;
        this.items = new ArrayList<>();
        this.selectedIndex = 0;
        this.displayFunction = displayFunction;
    }

    @Override
    public void render(TextGraphics graphics, int x, int y) {
        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        graphics.putString(x, y, title + ":");

        if (items.isEmpty()) {
            graphics.setForegroundColor(TextColor.ANSI.RED);
            graphics.putString(x + 2, y + 2, "No items available");
            return;
        }

        int currentY = y + 2;
        for (int i = 0; i < items.size(); i++) {
            T item = items.get(i);
            String display = displayFunction.apply(item);

            if (i == selectedIndex && focused) {
                graphics.setForegroundColor(TextColor.ANSI.GREEN_BRIGHT);
                graphics.putString(x + 2, currentY, ">> " + display);
            } else {
                graphics.setForegroundColor(TextColor.ANSI.WHITE);
                graphics.putString(x + 2, currentY, "   " + display);
            }
            currentY++;
        }
    }

    @Override
    public boolean handleInput(KeyStroke keyStroke) {
        if (!focused || items.isEmpty()) {
            return false;
        }

        if (keyStroke.getKeyType() == KeyType.ArrowUp) {
            selectedIndex = (selectedIndex - 1 + items.size()) % items.size();
            return true;
        } else if (keyStroke.getKeyType() == KeyType.ArrowDown) {
            selectedIndex = (selectedIndex + 1) % items.size();
            return true;
        }

        return false;
    }

    public void setItems(List<T> items) {
        this.items = new ArrayList<>(items);
        if (selectedIndex >= items.size()) {
            selectedIndex = Math.max(0, items.size() - 1);
        }
    }

    public T getSelectedItem() {
        if (items.isEmpty()) {
            return null;
        }
        return items.get(selectedIndex);
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int index) {
        if (index >= 0 && index < items.size()) {
            this.selectedIndex = index;
        }
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
