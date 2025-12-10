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
 * UC-10.04: Separate events that exist in target month but not in other months
 */
public class UC_10_04_ItemsForMonth_Success_SeparateMonthFilters_Test extends BaseIntegrationTest {
    @Autowired
    private CalendarService calendarService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CourseService courseService;

    @Test
    @DisplayName("UC-10.04: Filters tasks to only include target month")
    public void testItemsForMonthSuccessSeparateMonthFilters() {
        // Arrange - Create tasks in different months
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "calendar.uc1004@test.com",
                "Password123!"
        ));

        UUID courseId = courseService.createCourse("CS101", "Intro to CS", user.getId()).getId();

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        int currentYear = now.getYear();
        int currentMonthNum = now.getMonthValue();

        // Task in current month
        Instant currentMonthDeadline = now.plusDays(5).toInstant();
        courseService.addTaskToCourse(courseId, "Current Month Task", currentMonthDeadline, "Task 1");

        // Task in next month
        ZonedDateTime nextMonth = ZonedDateTime.now(ZoneId.of("UTC"));
        Instant nextMonthDeadline = nextMonth.plusMonths(1).withDayOfMonth(15).toInstant();
        courseService.addTaskToCourse(courseId, "Next Month Task", nextMonthDeadline, "Task 2");

        // Act - Query for current month only
        List<CalendarItemDTO> items = calendarService.getItemsForMonth(currentYear, currentMonthNum, user.getId());

        // Assert - Should only return current month task
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals("Current Month Task", items.getFirst().getTitle());
    }
}
