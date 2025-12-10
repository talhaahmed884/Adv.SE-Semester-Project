package com.cpp.project.uc_10_calendar_items_for_month;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.calendar.dto.CalendarItemDTO;
import com.cpp.project.calendar.service.CalendarService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.todolist.service.ToDoListService;
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
 * UC-10.33: Calendar excludes todo list tasks with null deadlines
 * Verifies that tasks without deadlines don't appear in calendar view
 */
public class UC_10_33_ItemsForMonth_Success_ExcludesNullDeadlines_Test extends BaseIntegrationTest {
    @Autowired
    private CalendarService calendarService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private ToDoListService todoListService;

    @Test
    @DisplayName("UC-10.33: Calendar excludes tasks with null deadlines")
    public void testItemsForMonthExcludesNullDeadlines() {
        // Arrange
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "calendar.uc1033@test.com",
                "Password123!"
        ));

        UUID todoListId = todoListService.createToDoList("My Tasks", user.getId()).getId();

        // Create task WITH deadline in January 2026
        Instant deadline = ZonedDateTime.of(2026, 1, 15, 10, 0, 0, 0,
                ZoneId.of("UTC")).toInstant();
        todoListService.addTaskToList(todoListId, "Task with deadline", deadline);

        // Create task WITHOUT deadline (null)
        todoListService.addTaskToList(todoListId, "Task without deadline", null);

        // Act - Query for January 2026
        List<CalendarItemDTO> items = calendarService.getItemsForMonth(2026, 1, user.getId(), "UTC");

        // Assert - Only task with deadline should appear
        assertNotNull(items);
        assertEquals(1, items.size(), "Should only include task with deadline");
        assertEquals("Task with deadline", items.getFirst().getTitle());
        assertEquals("TODO_LIST", items.getFirst().getSourceType());
    }
}
