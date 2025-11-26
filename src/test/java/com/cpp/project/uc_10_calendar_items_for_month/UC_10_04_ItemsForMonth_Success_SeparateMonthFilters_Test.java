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

        Calendar currentMonth = Calendar.getInstance();
        int currentYear = currentMonth.get(Calendar.YEAR);
        int currentMonthNum = currentMonth.get(Calendar.MONTH) + 1;

        // Task in current month
        currentMonth.add(Calendar.DAY_OF_MONTH, 5);
        Date currentMonthDeadline = currentMonth.getTime();
        courseService.addTaskToCourse(courseId, "Current Month Task", currentMonthDeadline, "Task 1");

        // Task in next month
        Calendar nextMonth = Calendar.getInstance();
        nextMonth.add(Calendar.MONTH, 1);
        nextMonth.set(Calendar.DAY_OF_MONTH, 15);
        Date nextMonthDeadline = nextMonth.getTime();
        courseService.addTaskToCourse(courseId, "Next Month Task", nextMonthDeadline, "Task 2");

        // Act - Query for current month only
        List<CalendarItemDTO> items = calendarService.getItemsForMonth(currentYear, currentMonthNum, user.getId());

        // Assert - Should only return current month task
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals("Current Month Task", items.getFirst().getTitle());
    }
}
