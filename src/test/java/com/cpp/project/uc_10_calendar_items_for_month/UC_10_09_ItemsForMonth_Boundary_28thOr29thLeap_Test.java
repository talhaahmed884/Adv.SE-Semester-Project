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
 * UC-10.09: Iterates February 28 for non-leap year correctly
 */
public class UC_10_09_ItemsForMonth_Boundary_28thOr29thLeap_Test extends BaseIntegrationTest {
    @Autowired
    private CalendarService calendarService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CourseService courseService;

    @Test
    @DisplayName("UC-10.09: Handles February 28th for non-leap year")
    public void testItemsForMonthBoundary28thOr29thLeap() {
        // Arrange - Create task on Feb 28 in non-leap year
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "calendar.uc1009@test.com",
                "Password123!"
        ));

        UUID courseId = courseService.createCourse("CS101", "Intro to CS", user.getId()).getId();

        // Use a non-leap year (2026)
        Instant feb28Deadline = ZonedDateTime.of(2026, 2, 28, 12, 0, 0, 0, ZoneId.of("UTC")).toInstant();

        courseService.addTaskToCourse(courseId, "Feb 28 Task", feb28Deadline, "Description");

        // Act
        List<CalendarItemDTO> items = calendarService.getItemsForMonth(2026, 2, user.getId(), "UTC");

        // Assert
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals("Feb 28 Task", items.getFirst().getTitle());
    }
}
