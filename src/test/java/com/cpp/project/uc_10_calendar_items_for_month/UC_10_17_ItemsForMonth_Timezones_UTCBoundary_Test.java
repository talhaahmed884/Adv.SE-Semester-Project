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

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * UC-10.17: Events spanning UTC change are provided in consistent manner
 */
public class UC_10_17_ItemsForMonth_Timezones_UTCBoundary_Test extends BaseIntegrationTest {
    @Autowired
    private CalendarService calendarService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CourseService courseService;

    @Test
    @DisplayName("UC-10.17: Handles UTC timezone boundaries correctly")
    public void testItemsForMonthTimezonesUTCBoundary() {
        // Arrange
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "calendar.uc1017@test.com",
                "Password123!"
        ));

        UUID courseId = courseService.createCourse("CS101", "Intro to CS", user.getId()).getId();

        // Create date at UTC boundary
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.set(2026, Calendar.MARCH, 10, 2, 0, 0); // DST boundary in US
        Date deadline = cal.getTime();

        courseService.addTaskToCourse(courseId, "UTC Boundary Task", deadline, "Description");

        // Act
        List<CalendarItemDTO> items = calendarService.getItemsForMonth(2026, 3, user.getId());

        // Assert - Should handle timezone correctly
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals("UTC Boundary Task", items.getFirst().getTitle());
    }
}
