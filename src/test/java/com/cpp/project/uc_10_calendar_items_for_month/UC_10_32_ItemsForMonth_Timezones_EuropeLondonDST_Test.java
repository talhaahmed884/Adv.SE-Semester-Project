package com.cpp.project.uc_10_calendar_items_for_month;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.calendar.dto.CalendarItemDTO;
import com.cpp.project.calendar.service.CalendarService;
import com.cpp.project.course.service.CourseService;
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
 * UC-10.32: Calendar items retrieved correctly with Europe/London timezone during DST
 * Tests timezone handling with British Summer Time (BST)
 */
public class UC_10_32_ItemsForMonth_Timezones_EuropeLondonDST_Test extends BaseIntegrationTest {
    @Autowired
    private CalendarService calendarService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private ToDoListService todoListService;

    @Test
    @DisplayName("UC-10.32: Returns items correctly for Europe/London timezone with DST")
    public void testItemsForMonthTimezonesEuropeLondonDST() {
        // Arrange
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "calendar.uc1032@test.com",
                "Password123!"
        ));

        UUID courseId = courseService.createCourse("CS101", "Intro to CS", user.getId()).getId();
        UUID todoListId = todoListService.createToDoList("My Tasks", user.getId()).getId();

        // Create task during BST (British Summer Time, UTC+1)
        // July 15, 2026 at 2:00 PM BST (1:00 PM UTC)
        Instant deadline1 = ZonedDateTime.of(2026, 7, 15, 14, 0, 0, 0,
                ZoneId.of("Europe/London")).toInstant();

        // Create task during GMT (winter, UTC+0)
        // December 20, 2026 at 2:00 PM GMT (2:00 PM UTC)
        Instant deadline2 = ZonedDateTime.of(2026, 12, 20, 14, 0, 0, 0,
                ZoneId.of("Europe/London")).toInstant();

        courseService.addTaskToCourse(courseId, "Summer Task", deadline1, "During BST");
        todoListService.addTaskToList(todoListId, "Winter Task", deadline2);

        // Act - Query for July 2026 (BST period)
        List<CalendarItemDTO> itemsJuly = calendarService.getItemsForMonth(
                2026, 7, user.getId(), "Europe/London");

        // Query for December 2026 (GMT period)
        List<CalendarItemDTO> itemsDecember = calendarService.getItemsForMonth(
                2026, 12, user.getId(), "Europe/London");

        // Assert - Should find correct tasks in correct months
        assertNotNull(itemsJuly);
        assertEquals(1, itemsJuly.size());
        assertEquals("Summer Task", itemsJuly.getFirst().getTitle());

        assertNotNull(itemsDecember);
        assertEquals(1, itemsDecember.size());
        assertEquals("Winter Task", itemsDecember.getFirst().getTitle());
    }
}
