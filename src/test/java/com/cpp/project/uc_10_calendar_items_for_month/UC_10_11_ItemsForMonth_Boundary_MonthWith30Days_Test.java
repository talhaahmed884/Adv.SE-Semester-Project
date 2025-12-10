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
 * UC-10.11: Iterates months with 30 days accurately
 */
public class UC_10_11_ItemsForMonth_Boundary_MonthWith30Days_Test extends BaseIntegrationTest {
    @Autowired
    private CalendarService calendarService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CourseService courseService;

    @Test
    @DisplayName("UC-10.11: Handles months with 30 days (April)")
    public void testItemsForMonthBoundaryMonthWith30Days() {
        // Arrange - Create task on 30th day of April
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "calendar.uc1011@test.com",
                "Password123!"
        ));

        UUID courseId = courseService.createCourse("CS101", "Intro to CS", user.getId()).getId();

        Instant april30Deadline = ZonedDateTime.of(2026, 4, 30, 12, 0, 0, 0, ZoneId.of("UTC")).toInstant();

        courseService.addTaskToCourse(courseId, "April 30 Task", april30Deadline, "Description");

        // Act
        List<CalendarItemDTO> items = calendarService.getItemsForMonth(2026, 4, user.getId(), "UTC");

        // Assert
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals("April 30 Task", items.getFirst().getTitle());
    }
}
