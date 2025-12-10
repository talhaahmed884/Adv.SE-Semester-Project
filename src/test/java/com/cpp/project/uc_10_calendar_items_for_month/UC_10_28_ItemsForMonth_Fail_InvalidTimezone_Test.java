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

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UC-10.28: Invalid timezone ID throws CalendarException
 */
public class UC_10_28_ItemsForMonth_Fail_InvalidTimezone_Test extends BaseIntegrationTest {
    @Autowired
    private CalendarService calendarService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-10.28: Invalid timezone ID throws CalendarException")
    public void testItemsForMonthFailInvalidTimezone() {
        // Arrange
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "calendar.uc1028@test.com",
                "Password123!"
        ));

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        int year = now.getYear();
        int month = now.getMonthValue();

        // Act & Assert - Should throw CalendarException for invalid timezone
        CalendarException exception = assertThrows(CalendarException.class, () -> calendarService.getItemsForMonth(year, month, user.getId(), "Invalid/Timezone"));

        assertTrue(exception.getMessage().contains("Invalid timezone"),
                "Exception message should mention invalid timezone");
    }
}
