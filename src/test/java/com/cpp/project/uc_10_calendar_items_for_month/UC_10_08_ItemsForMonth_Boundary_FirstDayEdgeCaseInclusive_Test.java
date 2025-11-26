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
 * UC-10.08: Event ending on 1st row result on 1st day in the boundary
 */
public class UC_10_08_ItemsForMonth_Boundary_FirstDayEdgeCaseInclusive_Test extends BaseIntegrationTest {
    @Autowired
    private CalendarService calendarService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CourseService courseService;

    @Test
    @DisplayName("UC-10.08: Includes task on first day of month (boundary)")
    public void testItemsForMonthBoundaryFirstDayEdgeCaseInclusive() {
        // Arrange - Create task on first day of month
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "calendar.uc1008@test.com",
                "Password123!"
        ));

        UUID courseId = courseService.createCourse("CS101", "Intro to CS", user.getId()).getId();

        Calendar cal = Calendar.getInstance();
        cal.set(2026, Calendar.FEBRUARY, 1);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;

        // Set to first day of month at 00:00:00
        cal.set(year, month - 1, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date firstDayDeadline = cal.getTime();

        courseService.addTaskToCourse(courseId, "First Day Task", firstDayDeadline, "Description");

        // Act
        List<CalendarItemDTO> items = calendarService.getItemsForMonth(year, month, user.getId());

        // Assert - Should include task on first day
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals("First Day Task", items.getFirst().getTitle());
    }
}
