package com.cpp.project.uc_8_add_todo_list_task;

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
 * UC-8.5.16: Returns never log raw description on PII
 */
public class UC_8_5_16_AddTask_Security_NoHtmlLogs_Test extends BaseIntegrationTest {
    @Autowired
    private ToDoListService toDoListService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-8.5.16: Never log raw description with PII")
    public void testAddTaskSecurityNoHtmlLogs() {
        // Arrange - Set up log capture
        Logger logger = (Logger) LoggerFactory.getLogger("com.cpp.project.todolist");
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        // Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.task.uc8516@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a todo list
        ToDoListDTO todoList = toDoListService.createToDoList("My Tasks", userId);

        // Create a future deadline
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date futureDeadline = calendar.getTime();

        // Clear previous logs
        listAppender.list.clear();

        // Act - Add task with sensitive PII information
        String sensitiveDescription = "Call doctor about SSN: 123-45-6789";
        ToDoListTaskDTO result = toDoListService.addTaskToList(
                todoList.getId(),
                sensitiveDescription,
                futureDeadline
        );

        // Assert
        assertNotNull(result);

        // Check that logs don't contain raw sensitive description
        String allLogs = listAppender.list.stream()
                .map(ILoggingEvent::getFormattedMessage)
                .reduce("", (a, b) -> a + " " + b);

        // Should not log the sensitive description
        assertFalse(allLogs.contains("SSN"),
                "Logs should not contain sensitive keywords");
        assertFalse(allLogs.contains("123-45-6789"),
                "Logs should not contain SSN number");

        // Clean up
        logger.detachAppender(listAppender);
    }
}
