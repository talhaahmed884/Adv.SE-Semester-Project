package com.cpp.project.common.validation.service;

import com.cpp.project.common.validation.entity.ValidationResult;
import com.cpp.project.common.validation.entity.ValidationResultBuilder;
import com.cpp.project.common.validation.entity.Validator;

import java.util.UUID;

/**
 * Validator for UUID fields
 * Template Method Pattern - extends Validator<UUID>
 */
public class UUIDValidator extends Validator<UUID> {
    private String fieldName = "UUID";

    /**
     * Validate a UUID with a custom field name
     *
     * @param value     The UUID to validate
     * @param fieldName The name of the field (for error messages)
     * @return ValidationResult
     */
    public ValidationResult validate(UUID value, String fieldName) {
        this.fieldName = fieldName;
        return super.validate(value);
    }

    @Override
    protected void performValidation(UUID value, ValidationResultBuilder resultBuilder) {
        if (value == null) {
            resultBuilder.addError(fieldName + " cannot be null");
        }
        // UUID type safety ensures valid format - no additional validation needed
    }
}
