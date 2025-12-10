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
 * UC-10.27: Task at beginning of next month in EST timezone appears in correct month
 */
public class UC_10_27_ItemsForMonth_Timezones_NextMonthBoundaryEST_Test extends BaseIntegrationTest {
    @Autowired
    private CalendarService calendarService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CourseService courseService;

    @Test
    @DisplayName("UC-10.27: Task at Feb 1 12:01 AM EST appears in February, not January")
    public void testItemsForMonthTimezonesNextMonthBoundaryEST() {
        // Arrange
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "calendar.uc1027@test.com",
                "Password123!"
        ));

        UUID courseId = courseService.createCourse("CS101", "Intro to CS", user.getId()).getId();

        // Create task on February 1, 2026 at 12:01 AM EST (05:01 AM UTC)
        Instant deadline = ZonedDateTime.of(2026, 2, 1, 0, 1, 0, 0,
                ZoneId.of("America/New_York")).toInstant();

        courseService.addTaskToCourse(courseId, "Next Month Task", deadline, "Description");

        // Act - Query for January 2026 in EST
        List<CalendarItemDTO> itemsJanuary = calendarService.getItemsForMonth(2026, 1, user.getId(), "America/New_York");

        // Query for February 2026 in EST
        List<CalendarItemDTO> itemsFebruary = calendarService.getItemsForMonth(2026, 2, user.getId(), "America/New_York");

        // Assert - Task should appear in February, NOT in January
        assertNotNull(itemsJanuary);
        assertEquals(0, itemsJanuary.size(), "Task at Feb 1 12:01 AM EST should NOT appear in January");

        assertNotNull(itemsFebruary);
        assertEquals(1, itemsFebruary.size());
        assertEquals("Next Month Task", itemsFebruary.getFirst().getTitle());
    }
}
