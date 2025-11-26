package com.cpp.project.calendar.validation;


import com.cpp.project.common.validation.entity.ValidationResultBuilder;
import com.cpp.project.common.validation.entity.Validator;

/**
 * Validator for calendar date parameters
 */
public class CalendarDateValidator extends Validator<CalendarDateValidator.DateParams> {

    @Override
    protected void performValidation(DateParams params, ValidationResultBuilder resultBuilder) {
        // Validate year
        if (params.year < 1900 || params.year > 2100) {
            resultBuilder.addError("Year must be between 1900 and 2100");
        }

        // Validate month
        if (params.month < 1 || params.month > 12) {
            resultBuilder.addError("Month must be between 1 and 12");
        }
    }

    public static class DateParams {
        public final int year;
        public final int month;

        public DateParams(int year, int month) {
            this.year = year;
            this.month = month;
        }
    }
}
