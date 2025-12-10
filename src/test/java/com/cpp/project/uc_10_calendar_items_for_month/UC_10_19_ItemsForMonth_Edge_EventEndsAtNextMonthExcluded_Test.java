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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UC-10.19: Event ending exactly at 00:00:00 on first day of next month excluded
 */
public class UC_10_19_ItemsForMonth_Edge_EventEndsAtNextMonthExcluded_Test extends BaseIntegrationTest {
    @Autowired
    private CalendarService calendarService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CourseService courseService;

    @Test
    @DisplayName("UC-10.19: Excludes event at 00:00:00 of next month")
    public void testItemsForMonthEdgeEventEndsAtNextMonthExcluded() {
        // Arrange
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "calendar.uc1019@test.com",
                "Password123!"
        ));

        UUID courseId = courseService.createCourse("CS101", "Intro to CS", user.getId()).getId();

        ZonedDateTime currentMonth = ZonedDateTime.now(ZoneId.of("UTC"));
        int year = currentMonth.getYear();
        int month = currentMonth.getMonthValue();

        // Create task at exactly 00:00:00 on 1st of NEXT month
        ZonedDateTime nextMonth = currentMonth.plusMonths(1).withDayOfMonth(1)
                .withHour(0).withMinute(0).withSecond(0).withNano(0);
        Instant nextMonthStart = nextMonth.toInstant();

        courseService.addTaskToCourse(courseId, "Next Month Task", nextMonthStart, "Description");

        // Act - Query for current month
        List<CalendarItemDTO> items = calendarService.getItemsForMonth(year, month, user.getId(), "UTC");

        // Assert - Should not include next month's task
        assertNotNull(items);
        assertTrue(items.isEmpty());
    }
}
