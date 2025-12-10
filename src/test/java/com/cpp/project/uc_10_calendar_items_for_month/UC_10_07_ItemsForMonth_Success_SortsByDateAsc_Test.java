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

import static org.junit.jupiter.api.Assertions.*;

/**
 * UC-10.07: Results are sorted by start time ascending, earliest finishing first
 */
public class UC_10_07_ItemsForMonth_Success_SortsByDateAsc_Test extends BaseIntegrationTest {
    @Autowired
    private CalendarService calendarService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CourseService courseService;

    @Test
    @DisplayName("UC-10.07: Returns items sorted by date ascending")
    public void testItemsForMonthSuccessSortsByDateAsc() {
        // Arrange - Create tasks with different deadlines
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "calendar.uc1007@test.com",
                "Password123!"
        ));

        UUID courseId = courseService.createCourse("CS101", "Intro to CS", user.getId()).getId();

        ZonedDateTime baseDate = ZonedDateTime.of(2026, 4, 5, 0, 0, 0, 0, ZoneId.of("UTC"));
        int year = baseDate.getYear();
        int month = baseDate.getMonthValue();

        // Add task with later deadline first
        Instant laterDeadline = baseDate.plusDays(15).toInstant();
        courseService.addTaskToCourse(courseId, "Later Task", laterDeadline, "Description");

        // Add task with earlier deadline
        Instant earlierDeadline = baseDate.plusDays(5).toInstant();
        courseService.addTaskToCourse(courseId, "Earlier Task", earlierDeadline, "Description");

        // Add task with middle deadline
        Instant middleDeadline = baseDate.plusDays(10).toInstant();
        courseService.addTaskToCourse(courseId, "Middle Task", middleDeadline, "Description");

        // Act
        List<CalendarItemDTO> items = calendarService.getItemsForMonth(year, month, user.getId(), "UTC");

        // Assert - Should be sorted by date ascending
        assertNotNull(items);
        assertEquals(3, items.size());
        assertEquals("Earlier Task", items.get(0).getTitle());
        assertEquals("Middle Task", items.get(1).getTitle());
        assertEquals("Later Task", items.get(2).getTitle());
        assertTrue(items.get(0).getDate().isBefore(items.get(1).getDate()));
        assertTrue(items.get(1).getDate().isBefore(items.get(2).getDate()));
    }
}
