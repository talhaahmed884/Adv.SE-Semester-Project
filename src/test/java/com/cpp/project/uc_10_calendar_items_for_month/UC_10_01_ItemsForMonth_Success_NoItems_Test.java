package com.cpp.project.uc_10_calendar_items_for_month;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.calendar.dto.CalendarItemDTO;
import com.cpp.project.calendar.service.CalendarService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.user.dto.SignUpRequestDTO;
import com.cpp.project.user.dto.UserDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UC-10.01: Returns empty list results when user has no items in month
 */
public class UC_10_01_ItemsForMonth_Success_NoItems_Test extends BaseIntegrationTest {
    @Autowired
    private CalendarService calendarService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-10.01: Returns empty list when user has no items in month")
    public void testItemsForMonthSuccessNoItems() {
        // Arrange - Create a user with no courses or todo lists
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "calendar.uc1001@test.com",
                "Password123!"
        ));

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;

        // Act
        List<CalendarItemDTO> items = calendarService.getItemsForMonth(year, month, user.getId());

        // Assert
        assertNotNull(items);
        assertTrue(items.isEmpty());
    }
}
