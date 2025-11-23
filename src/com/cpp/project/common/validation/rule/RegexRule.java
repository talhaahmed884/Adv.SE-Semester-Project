package com.cpp.project.common.validation.rule;

import com.cpp.project.common.validation.entity.ValidationRule;

import java.util.regex.Pattern;

public class RegexRule implements ValidationRule<String> {
    private final String fieldName;
    private final Pattern pattern;
    private final String patternDescription;

    public RegexRule(String fieldName, String regex, String patternDescription) {
        this.fieldName = fieldName;
        this.pattern = Pattern.compile(regex);
        this.patternDescription = patternDescription;
    }

    @Override
    public boolean isValid(String value) {
        return value != null && pattern.matcher(value).matches();
    }

    @Override
    public String getErrorMessage() {
        return fieldName + " must match pattern: " + patternDescription;
    }
}
