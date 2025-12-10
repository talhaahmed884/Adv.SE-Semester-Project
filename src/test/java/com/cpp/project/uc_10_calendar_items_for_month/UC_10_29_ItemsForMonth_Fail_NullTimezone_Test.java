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

/**
 * UC-10.29: Null timezone throws CalendarException
 */
public class UC_10_29_ItemsForMonth_Fail_NullTimezone_Test extends BaseIntegrationTest {
    @Autowired
    private CalendarService calendarService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-10.29: Null timezone throws CalendarException")
    public void testItemsForMonthFailNullTimezone() {
        // Arrange
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "calendar.uc1029@test.com",
                "Password123!"
        ));

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        int year = now.getYear();
        int month = now.getMonthValue();

        // Act & Assert - Should throw CalendarException for null timezone
        assertThrows(CalendarException.class, () -> calendarService.getItemsForMonth(year, month, user.getId(), null));
    }
}
