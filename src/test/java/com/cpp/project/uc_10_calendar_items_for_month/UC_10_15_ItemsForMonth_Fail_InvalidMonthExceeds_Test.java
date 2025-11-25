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

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * UC-10.15: Month > 12 are rejected
 */
public class UC_10_15_ItemsForMonth_Fail_InvalidMonthExceeds_Test extends BaseIntegrationTest {
    @Autowired
    private CalendarService calendarService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-10.15: Throws exception for month > 12")
    public void testItemsForMonthFailInvalidMonthExceeds() {
        // Arrange
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "calendar.uc1015@test.com",
                "Password123!"
        ));

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);

        // Act & Assert
        CalendarException exception = assertThrows(CalendarException.class, () -> {
            calendarService.getItemsForMonth(year, 13, user.getId());
        });

        assertEquals("CALENDAR_001", exception.getCode());
    }
}
