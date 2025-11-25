package com.cpp.project.common.sanitization.entity;

import java.util.Arrays;
import java.util.List;

/**
 * Composite Pattern - Chains multiple sanitization strategies together
 * Applies strategies in order, passing output of one to input of next
 */
public class CompositeSanitizationStrategy implements SanitizationStrategy {
    private final List<SanitizationStrategy> strategies;

    public CompositeSanitizationStrategy(SanitizationStrategy... strategies) {
        this.strategies = Arrays.asList(strategies);
    }

    public CompositeSanitizationStrategy(List<SanitizationStrategy> strategies) {
        this.strategies = strategies;
    }

    @Override
    public String sanitize(String value) {
        String result = value;
        for (SanitizationStrategy strategy : strategies) {
            result = strategy.sanitize(result);
        }
        return result;
    }
}
