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
 * UC-10.02: Returns single row events already inside the results
 */
public class UC_10_02_ItemsForMonth_Success_SingleCourseOnly_Test extends BaseIntegrationTest {
    @Autowired
    private CalendarService calendarService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CourseService courseService;

    @Test
    @DisplayName("UC-10.02: Returns single course task in month")
    public void testItemsForMonthSuccessSingleCourseOnly() {
        // Arrange - Create user and course with one task
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "calendar.uc1002@test.com",
                "Password123!"
        ));

        UUID courseId = courseService.createCourse("CS101", "Intro to CS", user.getId()).getId();

        ZonedDateTime taskCal = ZonedDateTime.now(ZoneId.of("UTC")).plusDays(5);
        Instant deadline = taskCal.toInstant();
        courseService.addTaskToCourse(courseId, "Assignment 1", deadline, "Complete assignment");

        int year = taskCal.getYear();
        int month = taskCal.getMonthValue();

        // Act
        List<CalendarItemDTO> items = calendarService.getItemsForMonth(year, month, user.getId(), "UTC");

        // Assert
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals("COURSE", items.getFirst().getSourceType());
        assertEquals("Assignment 1", items.getFirst().getTitle());
    }
}
