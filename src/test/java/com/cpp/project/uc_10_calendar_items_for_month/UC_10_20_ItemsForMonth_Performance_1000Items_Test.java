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

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UC-10.20: Handles 1,000 items and returns results without crash
 */
public class UC_10_20_ItemsForMonth_Performance_1000Items_Test extends BaseIntegrationTest {
    @Autowired
    private CalendarService calendarService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CourseService courseService;

    @Test
    @DisplayName("UC-10.20: Handles 10000 items performance test")
    public void testItemsForMonthPerformance1000Items() {
        // Arrange - Create user and course
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "calendar.uc1020@test.com",
                "Password123!"
        ));

        UUID courseId = courseService.createCourse("CS101", "Intro to CS", user.getId()).getId();

        Calendar cal = Calendar.getInstance();
        cal.set(2026, Calendar.APRIL, 1);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;

        // Add 1000 tasks in the same month
        for (int i = 0; i < 10000; i++) {
            Calendar taskCal = Calendar.getInstance();
            taskCal.set(2026, Calendar.APRIL, 1);
            taskCal.set(2026, month - 1, (i % 28) + 1, 12, 0, 0);
            Date deadline = taskCal.getTime();
            courseService.addTaskToCourse(courseId, "Task " + i, deadline, "Description " + i);
        }

        // Act - Measure performance
        long startTime = System.currentTimeMillis();
        List<CalendarItemDTO> items = calendarService.getItemsForMonth(year, month, user.getId());
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Assert
        assertNotNull(items);
        assertEquals(10000, items.size());
        assertTrue(duration < 5000, "Query took " + duration + "ms, expected < 5000ms");
    }
}
