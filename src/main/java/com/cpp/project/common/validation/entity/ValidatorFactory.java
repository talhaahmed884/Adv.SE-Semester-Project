package com.cpp.project.common.validation.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

// Factory Pattern for creating validators
public class ValidatorFactory {
    private static final Map<Class<?>, Function<?, Validator<?>>> validatorMap = new HashMap<>();

    public static <T> void registerValidator(Class<?> clazz, Function<?, Validator<?>> factory) {
        validatorMap.put(clazz, factory);
    }

    @SuppressWarnings("unchecked")
    public static <T> Validator<T> getValidator(Class<T> clazz) {
        Function<?, Validator<?>> factory = validatorMap.get(clazz);
        if (factory == null) {
            throw new IllegalArgumentException("No validator registered for class: " + clazz.getName());
        }
        return (Validator<T>) ((Function<?, Validator<?>>) factory).apply(null);
    }
}
