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
 * UC-10.25: Calendar items retrieved correctly with Asia/Tokyo timezone
 */
public class UC_10_25_ItemsForMonth_Timezones_AsiaTokyo_Test extends BaseIntegrationTest {
    @Autowired
    private CalendarService calendarService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CourseService courseService;

    @Test
    @DisplayName("UC-10.25: Returns items correctly for Asia/Tokyo timezone")
    public void testItemsForMonthTimezonesAsiaTokyo() {
        // Arrange
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "calendar.uc1025@test.com",
                "Password123!"
        ));

        UUID courseId = courseService.createCourse("CS101", "Intro to CS", user.getId()).getId();

        // Create task on March 20, 2026 at 10:00 AM JST (1:00 AM UTC)
        Instant deadline = ZonedDateTime.of(2026, 3, 20, 10, 0, 0, 0,
                ZoneId.of("Asia/Tokyo")).toInstant();

        courseService.addTaskToCourse(courseId, "JST Task", deadline, "Description");

        // Act - Query for March 2026 in JST
        List<CalendarItemDTO> items = calendarService.getItemsForMonth(2026, 3, user.getId(), "Asia/Tokyo");

        // Assert - Should find the task
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals("JST Task", items.getFirst().getTitle());
        assertEquals("COURSE", items.getFirst().getSourceType());
    }
}
