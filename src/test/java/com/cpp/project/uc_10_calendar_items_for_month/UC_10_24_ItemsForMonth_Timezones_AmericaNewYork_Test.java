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
 * UC-10.24: Calendar items retrieved correctly with America/New_York timezone
 */
public class UC_10_24_ItemsForMonth_Timezones_AmericaNewYork_Test extends BaseIntegrationTest {
    @Autowired
    private CalendarService calendarService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CourseService courseService;

    @Test
    @DisplayName("UC-10.24: Returns items correctly for America/New_York timezone")
    public void testItemsForMonthTimezonesAmericaNewYork() {
        // Arrange
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "calendar.uc1024@test.com",
                "Password123!"
        ));

        UUID courseId = courseService.createCourse("CS101", "Intro to CS", user.getId()).getId();

        // Create task on January 15, 2026 at 3:00 PM EST (8:00 PM UTC)
        Instant deadline = ZonedDateTime.of(2026, 1, 15, 15, 0, 0, 0,
                ZoneId.of("America/New_York")).toInstant();

        courseService.addTaskToCourse(courseId, "EST Task", deadline, "Description");

        // Act - Query for January 2026 in EST
        List<CalendarItemDTO> items = calendarService.getItemsForMonth(2026, 1, user.getId(), "America/New_York");

        // Assert - Should find the task
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals("EST Task", items.getFirst().getTitle());
        assertEquals("COURSE", items.getFirst().getSourceType());
    }
}
