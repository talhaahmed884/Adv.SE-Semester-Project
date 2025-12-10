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

import static org.junit.jupiter.api.Assertions.*;

/**
 * UC-10.03: Returns multiple todo list tasks within the month
 */
public class UC_10_03_ItemsForMonth_Success_MultipleSeparatedTodos_Test extends BaseIntegrationTest {
    @Autowired
    private CalendarService calendarService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private ToDoListService todoListService;

    @Test
    @DisplayName("UC-10.03: Returns multiple todo list tasks in month")
    public void testItemsForMonthSuccessMultipleSeparatedTodos() {
        // Arrange - Create user and todo list with multiple tasks
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "calendar.uc1003@test.com",
                "Password123!"
        ));

        UUID todoListId = todoListService.createToDoList("My Tasks", user.getId()).getId();

        ZonedDateTime nextMonth = ZonedDateTime.now(ZoneId.of("UTC")).plusMonths(1).withDayOfMonth(1);
        int year = nextMonth.getYear();
        int month = nextMonth.getMonthValue();

        // Add multiple tasks in the same month
        Instant deadline1 = nextMonth.plusDays(2).toInstant();
        todoListService.addTaskToList(todoListId, "Task 1", deadline1);

        Instant deadline2 = nextMonth.plusDays(5).toInstant();
        todoListService.addTaskToList(todoListId, "Task 2", deadline2);

        Instant deadline3 = nextMonth.plusDays(10).toInstant();
        todoListService.addTaskToList(todoListId, "Task 3", deadline3);

        // Act
        List<CalendarItemDTO> items = calendarService.getItemsForMonth(year, month, user.getId());

        // Assert
        assertNotNull(items);
        assertEquals(3, items.size());
        assertTrue(items.stream().allMatch(item -> "TODO_LIST".equals(item.getSourceType())));
    }
}
