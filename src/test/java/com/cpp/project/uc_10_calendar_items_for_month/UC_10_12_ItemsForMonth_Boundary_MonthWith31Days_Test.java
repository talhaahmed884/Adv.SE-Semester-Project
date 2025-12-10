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
 * UC-10.12: Iterates months with 31 days accurately
 */
public class UC_10_12_ItemsForMonth_Boundary_MonthWith31Days_Test extends BaseIntegrationTest {
    @Autowired
    private CalendarService calendarService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CourseService courseService;

    @Test
    @DisplayName("UC-10.12: Handles months with 31 days (January)")
    public void testItemsForMonthBoundaryMonthWith31Days() {
        // Arrange - Create task on 31st day of January
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "calendar.uc1012@test.com",
                "Password123!"
        ));

        UUID courseId = courseService.createCourse("CS101", "Intro to CS", user.getId()).getId();

        Instant jan31Deadline = ZonedDateTime.of(2026, 1, 31, 12, 0, 0, 0, ZoneId.of("UTC")).toInstant();

        courseService.addTaskToCourse(courseId, "Jan 31 Task", jan31Deadline, "Description");

        // Act
        List<CalendarItemDTO> items = calendarService.getItemsForMonth(2026, 1, user.getId(), "UTC");

        // Assert
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals("Jan 31 Task", items.getFirst().getTitle());
    }
}
