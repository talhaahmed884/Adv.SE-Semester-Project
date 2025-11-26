package com.cpp.project.uc_10_calendar_items_for_month;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.calendar.dto.CalendarItemDTO;
import com.cpp.project.calendar.service.CalendarService;
import com.cpp.project.course.service.CourseService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.user.dto.SignUpRequestDTO;
import com.cpp.project.user.dto.UserDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UC-10.21: No sensitive event details are leaked during authorization failure
 */
public class UC_10_21_ItemsForMonth_Security_NoPrivileges_Test extends BaseIntegrationTest {
    @Autowired
    private CalendarService calendarService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CourseService courseService;

    @Test
    @DisplayName("UC-10.21: No sensitive data leaked in logs")
    public void testItemsForMonthSecurityNoPrivileges() {
        // Arrange - Set up log capture
        Logger logger = (Logger) LoggerFactory.getLogger("com.cpp.project.calendar");
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "calendar.uc1021@test.com",
                "Password123!"
        ));

        UUID courseId = courseService.createCourse("CS101", "Intro to CS", user.getId()).getId();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 5);
        Date deadline = cal.getTime();

        courseService.addTaskToCourse(courseId, "Sensitive Task SSN:123-45-6789", deadline, "Description");

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;

        // Clear previous logs
        listAppender.list.clear();

        // Act
        List<CalendarItemDTO> items = calendarService.getItemsForMonth(year, month, user.getId());

        // Assert - Check that logs don't contain sensitive data
        assertNotNull(items);
        assertEquals(1, items.size());

        String allLogs = listAppender.list.stream()
                .map(ILoggingEvent::getFormattedMessage)
                .reduce("", (a, b) -> a + " " + b);

        assertFalse(allLogs.contains("SSN"), "Logs should not contain sensitive keywords");
        assertFalse(allLogs.contains("123-45-6789"), "Logs should not contain SSN number");

        // Clean up
        logger.detachAppender(listAppender);
    }
}
