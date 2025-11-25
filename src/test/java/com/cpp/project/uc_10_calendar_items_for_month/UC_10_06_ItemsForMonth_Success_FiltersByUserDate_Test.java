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
 * UC-10.06: Returns only items owned by a student with all data
 */
public class UC_10_06_ItemsForMonth_Success_FiltersByUserDate_Test extends BaseIntegrationTest {
    @Autowired
    private CalendarService calendarService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CourseService courseService;

    @Test
    @DisplayName("UC-10.06: Filters items by user, only returns user's items")
    public void testItemsForMonthSuccessFiltersByUserDate() {
        // Arrange - Create two users with their own courses
        UserDTO user1 = authenticationService.signUp(new SignUpRequestDTO(
                "User",
                "calendar.uc1006.user1@test.com",
                "Password123!"
        ));

        UserDTO user2 = authenticationService.signUp(new SignUpRequestDTO(
                "User",
                "calendar.uc1006.user2@test.com",
                "Password123!"
        ));

        // User 1's course and task
        UUID course1Id = courseService.createCourse("CS101", "User 1 Course", user1.getId()).getId();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 5);
        Date deadline = cal.getTime();
        courseService.addTaskToCourse(course1Id, "User 1 Task", deadline, "Description");

        // User 2's course and task
        UUID course2Id = courseService.createCourse("CS201", "User 2 Course", user2.getId()).getId();
        courseService.addTaskToCourse(course2Id, "User 2 Task", deadline, "Description");

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;

        // Act - Get calendar items for user1
        List<CalendarItemDTO> user1Items = calendarService.getItemsForMonth(year, month, user1.getId());

        // Assert - Should only return user1's items
        assertNotNull(user1Items);
        assertEquals(1, user1Items.size());
        assertEquals("User 1 Task", user1Items.getFirst().getTitle());
    }
}
