package com.cpp.project.uc_6_mark_course_task_complete;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.course.dto.CourseDTO;
import com.cpp.project.course.dto.CourseTaskDTO;
import com.cpp.project.course.entity.CourseException;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * UC-6.4.17: No sensitive fields (name/description) or unique IDs in logs on failures
 */
public class UC_6_4_17_NoSensitiveLogging_OnFailures_Test extends BaseIntegrationTest {
    @Autowired
    private CourseService courseService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-6.4.17: No sensitive information logged on failures")
    public void testNoSensitiveLoggingOnFailures() {
        // Arrange - Set up log capture
        Logger logger = (Logger) LoggerFactory.getLogger("com.cpp.project.course");
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        // Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.complete.uc6417@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a course
        CourseDTO course = courseService.createCourse("CS106", "Database Systems", userId);

        // Create a future deadline
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date futureDeadline = calendar.getTime();

        // Add a task with sensitive information
        String sensitiveTaskName = "Confidential Assignment - Secret Project Alpha";
        String sensitiveDescription = "Access credentials: admin@example.com / password123";
        CourseTaskDTO task = courseService.addTaskToCourse(
                course.getId(),
                sensitiveTaskName,
                futureDeadline,
                sensitiveDescription
        );

        // Clear previous logs
        listAppender.list.clear();

        // Act - Try to mark a non-existent task as complete (should fail)
        UUID nonExistentTaskId = UUID.randomUUID();
        assertThrows(CourseException.class, () -> {
            courseService.markTaskComplete(course.getId(), nonExistentTaskId);
        });

        // Assert - Check that logs don't contain sensitive information
        String allLogs = listAppender.list.stream()
                .map(ILoggingEvent::getFormattedMessage)
                .reduce("", (a, b) -> a + " " + b);

        // Should not log task names or descriptions
        assertFalse(allLogs.contains(sensitiveTaskName),
                "Logs should not contain sensitive task name");
        assertFalse(allLogs.contains(sensitiveDescription),
                "Logs should not contain sensitive task description");
        assertFalse(allLogs.contains("Confidential"),
                "Logs should not contain sensitive keywords from task name");
        assertFalse(allLogs.contains("password123"),
                "Logs should not contain credentials from task description");

        // Clean up
        logger.detachAppender(listAppender);
    }
}
