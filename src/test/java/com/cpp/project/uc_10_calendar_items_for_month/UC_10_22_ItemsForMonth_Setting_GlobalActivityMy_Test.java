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
 * UC-10.22: Status test when the items are in ongoing state
 */
public class UC_10_22_ItemsForMonth_Setting_GlobalActivityMy_Test extends BaseIntegrationTest {
    @Autowired
    private CalendarService calendarService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CourseService courseService;

    @Test
    @DisplayName("UC-10.22: Returns items with correct status information")
    public void testItemsForMonthSettingGlobalActivityMy() {
        // Arrange
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "calendar.uc1022@test.com",
                "Password123!"
        ));

        UUID courseId = courseService.createCourse("CS101", "Intro to CS", user.getId()).getId();

        ZonedDateTime nowUTC = ZonedDateTime.now(ZoneId.of("UTC"));
        Instant deadline = nowUTC.plusDays(5).toInstant();

        UUID taskId = courseService.addTaskToCourse(courseId, "Task", deadline, "Description").getId();

        // Update task to IN_PROGRESS
        courseService.updateTaskProgress(courseId, taskId, 50);

        int year = nowUTC.getYear();
        int month = nowUTC.getMonthValue();

        // Act
        List<CalendarItemDTO> items = calendarService.getItemsForMonth(year, month, user.getId());

        // Assert - Should include status information
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals("IN_PROGRESS", items.getFirst().getStatus());
    }
}
