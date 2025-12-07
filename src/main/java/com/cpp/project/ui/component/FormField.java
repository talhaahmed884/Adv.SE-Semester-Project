package com.cpp.project.ui.component;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

/**
 * Component Pattern: Reusable form field component
 * Handles text input with label
 */
public class FormField extends AbstractComponent {
    private final String label;
    private final InputValidator validator;
    private final boolean maskInput;
    private StringBuilder value;

    public FormField(String label) {
        this(label, null, false);
    }

    public FormField(String label, InputValidator validator, boolean maskInput) {
        super(1);
        this.label = label;
        this.value = new StringBuilder();
        this.validator = validator;
        this.maskInput = maskInput;
    }

    @Override
    public void render(TextGraphics graphics, int x, int y) {
        TextColor color = focused ? TextColor.ANSI.YELLOW_BRIGHT : TextColor.ANSI.WHITE;
        graphics.setForegroundColor(color);

        graphics.putString(x, y, label + ": ");

        String displayValue = maskInput ? "*".repeat(value.length()) : value.toString();
        graphics.putString(x + label.length() + 2, y, displayValue + (focused ? "_" : ""));
    }

    @Override
    public boolean handleInput(KeyStroke keyStroke) {
        if (!focused) {
            return false;
        }

        if (keyStroke.getKeyType() == KeyType.Character) {
            char c = keyStroke.getCharacter();
            if (validator == null || validator.isValidCharacter(c, value.toString())) {
                value.append(c);
                return true;
            }
        } else if (keyStroke.getKeyType() == KeyType.Backspace && !value.isEmpty()) {
            value.deleteCharAt(value.length() - 1);
            return true;
        }

        return false;
    }

    public String getValue() {
        return value.toString();
    }

    public void setValue(String value) {
        this.value = new StringBuilder(value);
    }

    public void clear() {
        this.value = new StringBuilder();
    }

    /**
     * Strategy Pattern: Input validation strategy
     */
    public interface InputValidator {
        boolean isValidCharacter(char c, String currentValue);
    }
}
