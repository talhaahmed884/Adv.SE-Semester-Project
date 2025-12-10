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
 * UC-10.31: Same task appears in different months when queried with different timezones
 * Tests that a task at a timezone boundary appears in correct month based on timezone
 */
public class UC_10_31_ItemsForMonth_Timezones_SameTaskDifferentMonths_Test extends BaseIntegrationTest {
    @Autowired
    private CalendarService calendarService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CourseService courseService;

    @Test
    @DisplayName("UC-10.31: Same task appears in different months for different timezones")
    public void testItemsForMonthTimezonesSameTaskDifferentMonths() {
        // Arrange
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "calendar.uc1031@test.com",
                "Password123!"
        ));

        UUID courseId = courseService.createCourse("CS101", "Intro to CS", user.getId()).getId();

        // Create task on March 1, 2026 at 2:00 AM UTC
        // This is:
        // - Feb 28, 2026 9:00 PM EST (America/New_York, UTC-5)
        // - March 1, 2026 11:00 AM JST (Asia/Tokyo, UTC+9)
        Instant deadline = ZonedDateTime.of(2026, 3, 1, 2, 0, 0, 0,
                ZoneId.of("UTC")).toInstant();

        courseService.addTaskToCourse(courseId, "Boundary Task", deadline, "Description");

        // Act - Query for February 2026 in EST
        List<CalendarItemDTO> itemsFebruaryEST = calendarService.getItemsForMonth(
                2026, 2, user.getId(), "America/New_York");

        // Query for March 2026 in EST
        List<CalendarItemDTO> itemsMarchEST = calendarService.getItemsForMonth(
                2026, 3, user.getId(), "America/New_York");

        // Query for February 2026 in JST
        List<CalendarItemDTO> itemsFebruaryJST = calendarService.getItemsForMonth(
                2026, 2, user.getId(), "Asia/Tokyo");

        // Query for March 2026 in JST
        List<CalendarItemDTO> itemsMarchJST = calendarService.getItemsForMonth(
                2026, 3, user.getId(), "Asia/Tokyo");

        // Assert - Task appears in February for EST, March for JST
        assertNotNull(itemsFebruaryEST);
        assertEquals(1, itemsFebruaryEST.size(), "Task should appear in February when queried with EST");
        assertEquals("Boundary Task", itemsFebruaryEST.getFirst().getTitle());

        assertNotNull(itemsMarchEST);
        assertEquals(0, itemsMarchEST.size(), "Task should NOT appear in March when queried with EST");

        assertNotNull(itemsFebruaryJST);
        assertEquals(0, itemsFebruaryJST.size(), "Task should NOT appear in February when queried with JST");

        assertNotNull(itemsMarchJST);
        assertEquals(1, itemsMarchJST.size(), "Task should appear in March when queried with JST");
        assertEquals("Boundary Task", itemsMarchJST.getFirst().getTitle());
    }
}
