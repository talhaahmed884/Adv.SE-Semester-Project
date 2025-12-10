package com.cpp.project.uc_10_calendar_items_for_month;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.calendar.dto.CalendarItemDTO;
import com.cpp.project.calendar.service.CalendarService;
import com.cpp.project.course.service.CourseService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.user.dto.SignUpRequestDTO;
import com.cpp.project.user.dto.UserDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * UC-10.26: Task at month-end in EST timezone appears in correct month
 * This test verifies the fix for the bug where tasks at Jan 31 11:59 PM EST
 * were appearing in February instead of January
 */
public class UC_10_26_ItemsForMonth_Timezones_MonthEndBoundaryEST_Test extends BaseIntegrationTest {
    @Autowired
    private CalendarService calendarService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CourseService courseService;

    @Test
    @DisplayName("UC-10.26: Task at Jan 31 11:59 PM EST appears in January when queried with EST")
    public void testItemsForMonthTimezonesMonthEndBoundaryEST() {
        // Arrange
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "calendar.uc1026@test.com",
                "Password123!"
        ));

        UUID courseId = courseService.createCourse("CS101", "Intro to CS", user.getId()).getId();

        // Create task on January 31, 2026 at 11:59 PM EST (Feb 1, 2026 at 04:59 AM UTC)
        Instant deadline = ZonedDateTime.of(2026, 1, 31, 23, 59, 0, 0,
                ZoneId.of("America/New_York")).toInstant();

        courseService.addTaskToCourse(courseId, "Month End Task", deadline, "Description");

        // Act - Query for January 2026 in EST
        List<CalendarItemDTO> itemsJanuary = calendarService.getItemsForMonth(2026, 1, user.getId(), "America/New_York");

        // Query for February 2026 in EST
        List<CalendarItemDTO> itemsFebruary = calendarService.getItemsForMonth(2026, 2, user.getId(), "America/New_York");

        // Assert - Task should appear in January, NOT in February
        assertNotNull(itemsJanuary);
        assertEquals(1, itemsJanuary.size());
        assertEquals("Month End Task", itemsJanuary.getFirst().getTitle());

        assertNotNull(itemsFebruary);
        assertEquals(0, itemsFebruary.size(), "Task at Jan 31 11:59 PM EST should NOT appear in February");
    }
}
