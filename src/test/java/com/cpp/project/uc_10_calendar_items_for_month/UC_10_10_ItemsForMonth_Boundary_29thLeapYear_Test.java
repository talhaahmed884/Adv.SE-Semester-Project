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
 * UC-10.10: Iterates February 29 for leap year correctly
 */
public class UC_10_10_ItemsForMonth_Boundary_29thLeapYear_Test extends BaseIntegrationTest {
    @Autowired
    private CalendarService calendarService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CourseService courseService;

    @Test
    @DisplayName("UC-10.10: Handles February 29th for leap year")
    public void testItemsForMonthBoundary29thLeapYear() {
        // Arrange - Create task on Feb 29 in leap year
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "calendar.uc1010@test.com",
                "Password123!"
        ));

        UUID courseId = courseService.createCourse("CS101", "Intro to CS", user.getId()).getId();

        // Use a leap year (2024)
        Calendar cal = Calendar.getInstance();
        cal.set(2028, Calendar.FEBRUARY, 29, 12, 0, 0);
        Date feb29Deadline = cal.getTime();

        courseService.addTaskToCourse(courseId, "Feb 29 Task", feb29Deadline, "Description");

        // Act
        List<CalendarItemDTO> items = calendarService.getItemsForMonth(2028, 2, user.getId());

        // Assert
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals("Feb 29 Task", items.getFirst().getTitle());
    }
}
