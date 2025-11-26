package com.cpp.project.uc_10_calendar_items_for_month;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.calendar.entity.CalendarException;
import com.cpp.project.calendar.service.CalendarService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.user.dto.SignUpRequestDTO;
import com.cpp.project.user.dto.UserDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * UC-10.16: Year outside valid range (1900-2100) rejected
 */
public class UC_10_16_ItemsForMonth_Fail_InvalidYearTooLowOrHigh_Test extends BaseIntegrationTest {
    @Autowired
    private CalendarService calendarService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-10.16: Throws exception for year too low")
    public void testItemsForMonthFailInvalidYearTooLow() {
        // Arrange
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "calendar.uc1016a@test.com",
                "Password123!"
        ));

        // Act & Assert
        CalendarException exception = assertThrows(CalendarException.class, () -> {
            calendarService.getItemsForMonth(1899, 1, user.getId());
        });

        assertEquals("CALENDAR_001", exception.getCode());
    }

    @Test
    @DisplayName("UC-10.16: Throws exception for year too high")
    public void testItemsForMonthFailInvalidYearTooHigh() {
        // Arrange
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "calendar.uc1016b@test.com",
                "Password123!"
        ));

        // Act & Assert
        CalendarException exception = assertThrows(CalendarException.class, () -> {
            calendarService.getItemsForMonth(2101, 1, user.getId());
        });

        assertEquals("CALENDAR_001", exception.getCode());
    }
}
