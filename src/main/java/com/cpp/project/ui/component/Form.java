package com.cpp.project.ui.component;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.util.ArrayList;
import java.util.List;

/**
 * Composite Pattern: Form composed of multiple form fields
 * Mediator Pattern: Coordinates focus between fields
 */
public class Form extends AbstractComponent {
    private final List<UIComponent> fields;
    private int focusedFieldIndex;

    public Form() {
        super(0);
        this.fields = new ArrayList<>();
        this.focusedFieldIndex = 0;
    }

    public Form addField(UIComponent field) {
        fields.add(field);
        height += field.getHeight() + 1; // +1 for spacing
        return this;
    }

    @Override
    public void render(TextGraphics graphics, int x, int y) {
        int currentY = y;
        for (UIComponent field : fields) {
            field.render(graphics, x, currentY);
            currentY += field.getHeight() + 1;
        }
    }

    @Override
    public boolean handleInput(KeyStroke keyStroke) {
        if (!focused || fields.isEmpty()) {
            return false;
        }

        // Handle tab to move between fields
        if (keyStroke.getKeyType() == KeyType.Tab) {
            nextField();
            return true;
        }

        // Delegate to focused field
        UIComponent focusedField = fields.get(focusedFieldIndex);
        return focusedField.handleInput(keyStroke);
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);

        if (focused && !fields.isEmpty()) {
            fields.get(focusedFieldIndex).setFocused(true);
        } else {
            fields.forEach(field -> field.setFocused(false));
        }
    }

    private void nextField() {
        if (fields.isEmpty()) {
            return;
        }

        fields.get(focusedFieldIndex).setFocused(false);
        focusedFieldIndex = (focusedFieldIndex + 1) % fields.size();
        fields.get(focusedFieldIndex).setFocused(true);
    }

    public UIComponent getField(int index) {
        return fields.get(index);
    }

    public int getFieldCount() {
        return fields.size();
    }

    public void clear() {
        for (UIComponent field : fields) {
            if (field instanceof FormField) {
                ((FormField) field).clear();
            } else if (field instanceof DateInput) {
                ((DateInput) field).clear();
            }
        }
        focusedFieldIndex = 0;
        setFocused(false);
    }
}
