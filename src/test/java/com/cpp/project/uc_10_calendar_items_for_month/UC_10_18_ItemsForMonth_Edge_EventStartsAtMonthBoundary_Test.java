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
 * UC-10.18: Event starting exactly at 00:00:00 on the 1st included in the results
 */
public class UC_10_18_ItemsForMonth_Edge_EventStartsAtMonthBoundary_Test extends BaseIntegrationTest {
    @Autowired
    private CalendarService calendarService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CourseService courseService;

    @Test
    @DisplayName("UC-10.18: Includes event starting at 00:00:00 on 1st of month")
    public void testItemsForMonthEdgeEventStartsAtMonthBoundary() {
        // Arrange
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "calendar.uc1018@test.com",
                "Password123!"
        ));

        UUID courseId = courseService.createCourse("CS101", "Intro to CS", user.getId()).getId();

        // Create task at exactly 00:00:00 on 1st of month
        ZonedDateTime exactStart = ZonedDateTime.of(2026, 3, 1, 0, 0, 0, 0, ZoneId.of("UTC"));
        int year = exactStart.getYear();
        int month = exactStart.getMonthValue();
        Instant exactStartInstant = exactStart.toInstant();

        courseService.addTaskToCourse(courseId, "Exact Start Task", exactStartInstant, "Description");

        // Act
        List<CalendarItemDTO> items = calendarService.getItemsForMonth(year, month, user.getId());

        // Assert
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals("Exact Start Task", items.getFirst().getTitle());
    }
}
