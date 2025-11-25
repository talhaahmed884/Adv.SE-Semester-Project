package com.cpp.project.uc_9_mark_todo_list_task_complete;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.todolist.dto.ToDoListDTO;
import com.cpp.project.todolist.dto.ToDoListTaskDTO;
import com.cpp.project.todolist.service.ToDoListService;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * UC-9.6.09: No PII (description/deadline) is logged when markComplete is invoked
 */
public class UC_9_6_09_MarkComplete_Security_NoHtmlLogs_Test extends BaseIntegrationTest {
    @Autowired
    private ToDoListService toDoListService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-9.6.09: No sensitive information logged on mark complete")
    public void testMarkCompleteSecurityNoHtmlLogs() {
        // Arrange - Set up log capture
        Logger logger = (Logger) LoggerFactory.getLogger("com.cpp.project.todolist");
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        // Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.complete.uc9609@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a todo list
        ToDoListDTO todoList = toDoListService.createToDoList("My Tasks", userId);

        // Create a future deadline
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date futureDeadline = calendar.getTime();

        // Add a task with sensitive information
        String sensitiveDescription = "Call bank about account SSN: 123-45-6789";
        ToDoListTaskDTO task = toDoListService.addTaskToList(
                todoList.getId(),
                sensitiveDescription,
                futureDeadline
        );

        // Clear previous logs
        listAppender.list.clear();

        // Act - Mark task as complete
        ToDoListTaskDTO result = toDoListService.markTaskComplete(todoList.getId(), task.getId());

        // Assert
        assertNotNull(result);

        // Check that logs don't contain sensitive description
        String allLogs = listAppender.list.stream()
                .map(ILoggingEvent::getFormattedMessage)
                .reduce("", (a, b) -> a + " " + b);

        assertFalse(allLogs.contains("SSN"),
                "Logs should not contain sensitive keywords");
        assertFalse(allLogs.contains("123-45-6789"),
                "Logs should not contain SSN number");

        // Clean up
        logger.detachAppender(listAppender);
    }
}
