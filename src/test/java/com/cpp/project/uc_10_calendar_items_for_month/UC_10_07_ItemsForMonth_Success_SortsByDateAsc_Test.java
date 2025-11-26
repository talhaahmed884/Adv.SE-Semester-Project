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

        Calendar cal = Calendar.getInstance();
        cal.set(2026, Calendar.MARCH, 5);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;

        // Add task with later deadline first
        cal.add(Calendar.DAY_OF_MONTH, 15);
        Date laterDeadline = cal.getTime();
        courseService.addTaskToCourse(courseId, "Later Task", laterDeadline, "Description");

        // Add task with earlier deadline
        Calendar earlierCal = Calendar.getInstance();
        earlierCal.set(2026, Calendar.MARCH, 5);
        earlierCal.add(Calendar.DAY_OF_MONTH, 5);
        Date earlierDeadline = earlierCal.getTime();
        courseService.addTaskToCourse(courseId, "Earlier Task", earlierDeadline, "Description");

        // Add task with middle deadline
        Calendar middleCal = Calendar.getInstance();
        middleCal.set(2026, Calendar.MARCH, 5);
        middleCal.add(Calendar.DAY_OF_MONTH, 10);
        Date middleDeadline = middleCal.getTime();
        courseService.addTaskToCourse(courseId, "Middle Task", middleDeadline, "Description");

        // Act
        List<CalendarItemDTO> items = calendarService.getItemsForMonth(year, month, user.getId());

        // Assert - Should be sorted by date ascending
        assertNotNull(items);
        assertEquals(3, items.size());
        assertEquals("Earlier Task", items.get(0).getTitle());
        assertEquals("Middle Task", items.get(1).getTitle());
        assertEquals("Later Task", items.get(2).getTitle());
        assertTrue(items.get(0).getDate().before(items.get(1).getDate()));
        assertTrue(items.get(1).getDate().before(items.get(2).getDate()));
    }
}
