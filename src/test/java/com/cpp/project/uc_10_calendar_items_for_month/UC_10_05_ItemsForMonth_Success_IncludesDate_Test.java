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
 * UC-10.05: Includes events that start in target month and row results
 */
public class UC_10_05_ItemsForMonth_Success_IncludesDate_Test extends BaseIntegrationTest {
    @Autowired
    private CalendarService calendarService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CourseService courseService;

    @Test
    @DisplayName("UC-10.05: Includes date information in calendar items")
    public void testItemsForMonthSuccessIncludesDate() {
        // Arrange
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "calendar.uc1005@test.com",
                "Password123!"
        ));

        UUID courseId = courseService.createCourse("CS101", "Intro to CS", user.getId()).getId();

        ZonedDateTime nowUTC = ZonedDateTime.now(ZoneId.of("UTC"));
        Instant deadline = nowUTC.plusDays(7).toInstant();

        courseService.addTaskToCourse(courseId, "Assignment", deadline, "Description");

        int year = nowUTC.getYear();
        int month = nowUTC.getMonthValue();

        // Act
        List<CalendarItemDTO> items = calendarService.getItemsForMonth(year, month, user.getId());

        // Assert
        assertNotNull(items);
        assertEquals(1, items.size());
        assertNotNull(items.getFirst().getDate());
        assertEquals(deadline, items.getFirst().getDate());
    }
}
