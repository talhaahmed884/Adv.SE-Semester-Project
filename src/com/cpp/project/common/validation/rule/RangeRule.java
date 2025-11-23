package com.cpp.project.common.validation.rule;

import com.cpp.project.common.validation.entity.ValidationRule;

public class RangeRule<T extends Comparable<T>> implements ValidationRule<T> {
    private final String fieldName;
    private final T min;
    private final T max;

    public RangeRule(String fieldName, T min, T max) {
        this.fieldName = fieldName;
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean isValid(T value) {
        if (value == null) return false;
        return value.compareTo(min) >= 0 && value.compareTo(max) <= 0;
    }

    @Override
    public String getErrorMessage() {
        return fieldName + " must be between " + min + " and " + max;
    }
}
