package com.cpp.project.uc_10_calendar_items_for_month;

import com.cpp.project.calendar.dto.CalendarItemDTO;
import com.cpp.project.calendar.service.CalendarService;
import com.cpp.project.entity.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UC-10.13: Not user is expired (fails gracefully)
 */
public class UC_10_13_ItemsForMonth_Fail_NoUser_Test extends BaseIntegrationTest {
    @Autowired
    private CalendarService calendarService;

    @Test
    @DisplayName("UC-10.13: Returns empty list for non-existent user")
    public void testItemsForMonthFailNoUser() {
        // Arrange - Use a non-existent user ID
        UUID nonExistentUserId = UUID.randomUUID();

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;

        // Act
        List<CalendarItemDTO> items = calendarService.getItemsForMonth(year, month, nonExistentUserId);

        // Assert - Should return empty list (no exception thrown)
        assertNotNull(items);
        assertTrue(items.isEmpty());
    }
}
