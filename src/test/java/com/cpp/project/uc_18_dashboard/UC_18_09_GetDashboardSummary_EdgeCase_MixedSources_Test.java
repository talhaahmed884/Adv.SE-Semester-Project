package com.cpp.project.uc_18_dashboard;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.course.dto.CourseDTO;
import com.cpp.project.course.service.CourseService;
import com.cpp.project.dashboard.dto.DashboardSummaryDTO;
import com.cpp.project.dashboard.dto.UpcomingTaskDTO;
import com.cpp.project.dashboard.service.DashboardService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.todolist.dto.ToDoListDTO;
import com.cpp.project.todolist.service.ToDoListService;
import com.cpp.project.user.dto.SignUpRequestDTO;
import com.cpp.project.user.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * UC-18.10: Edge case - Mix of course tasks and todo list tasks
 */
public class UC_18_09_GetDashboardSummary_EdgeCase_MixedSources_Test extends BaseIntegrationTest {
    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private ToDoListService todoListService;

    private UserDTO testUser;

    @BeforeEach
    public void setUp() {
        testUser = authenticationService.signUp(new SignUpRequestDTO(
                "Mixed Sources Test User",
                "mixed.sources.uc1810@test.com",
                "Password123!"
        ));
    }

    @Test
    @DisplayName("UC-18.10: Dashboard correctly aggregates tasks from both courses and todo lists")
    public void testMixedSourcesAggregation() {
        // Arrange - Create course tasks
        CourseDTO course1 = courseService.createCourse("CS101", "Course 1", testUser.getId());
        courseService.addTaskToCourse(
                course1.getId(),
                "Course Task 1",
                Instant.now().plus(1, ChronoUnit.DAYS),
                "From course"
        );

        CourseDTO course2 = courseService.createCourse("CS202", "Course 2", testUser.getId());
        courseService.addTaskToCourse(
                course2.getId(),
                "Course Task 2",
                Instant.now().plus(3, ChronoUnit.DAYS),
                "From course"
        );

        // Create todo list tasks
        ToDoListDTO todoList1 = todoListService.createToDoList("Personal", testUser.getId());
        todoListService.addTaskToList(
                todoList1.getId(),
                "Todo Task 1",
                Instant.now().plus(2, ChronoUnit.DAYS)
        );

        ToDoListDTO todoList2 = todoListService.createToDoList("Work", testUser.getId());
        todoListService.addTaskToList(
                todoList2.getId(),
                "Todo Task 2",
                Instant.now().plus(4, ChronoUnit.DAYS)
        );

        // Act
        DashboardSummaryDTO result = dashboardService.getDashboardSummary(testUser.getId());

        // Assert - All 4 tasks should appear
        List<UpcomingTaskDTO> upcomingTasks = result.getUpcomingTasks();
        assertEquals(4, upcomingTasks.size());

        // Verify we have tasks from both sources
        long courseTaskCount = upcomingTasks.stream()
                .filter(t -> t.getSourceType().equals("COURSE"))
                .count();
        long todoTaskCount = upcomingTasks.stream()
                .filter(t -> t.getSourceType().equals("TODO_LIST"))
                .count();

        assertEquals(2, courseTaskCount, "Should have 2 course tasks");
        assertEquals(2, todoTaskCount, "Should have 2 todo list tasks");

        // Verify source names are correct
        UpcomingTaskDTO courseTask = upcomingTasks.stream()
                .filter(t -> t.getTaskName().equals("Course Task 1"))
                .findFirst()
                .orElse(null);
        assertNotNull(courseTask);
        assertEquals("COURSE", courseTask.getSourceType());
        assertEquals("Course 1", courseTask.getSourceName());

        UpcomingTaskDTO todoTask = upcomingTasks.stream()
                .filter(t -> t.getTaskName().equals("Todo Task 1"))
                .findFirst()
                .orElse(null);
        assertNotNull(todoTask);
        assertEquals("TODO_LIST", todoTask.getSourceType());
        assertEquals("Personal", todoTask.getSourceName());
    }
}
