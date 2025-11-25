package com.cpp.project.uc_4_add_course_task;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.course.dto.CourseDTO;
import com.cpp.project.course.dto.CourseTaskDTO;
import com.cpp.project.course.service.CourseService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.user.dto.SignUpRequestDTO;
import com.cpp.project.user.dto.UserDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UC-4.11: Documents behavior: duplicate task names are allowed within a course
 */
public class UC_4_11_AddCourseTask_Info_DuplicateNameAllowed_Test extends BaseIntegrationTest {
    @Autowired
    private CourseService courseService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-4.11: Allows duplicate task names within a course")
    public void testAddCourseTaskWithDuplicateName() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.task.uc411@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a course
        CourseDTO course = courseService.createCourse("CS101", "Introduction to CS", userId);

        // Create a future deadline
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date futureDeadline = calendar.getTime();

        // Act - Add first task with name "HW1"
        CourseTaskDTO task1 = courseService.addTaskToCourse(
                course.getId(),
                "HW1",
                futureDeadline,
                "First homework"
        );

        // Add second task with same name "HW1"
        CourseTaskDTO task2 = courseService.addTaskToCourse(
                course.getId(),
                "HW1",
                futureDeadline,
                "Second homework with same name"
        );

        // Assert - Both tasks should be created successfully
        assertNotNull(task1);
        assertNotNull(task2);
        assertEquals("HW1", task1.getName());
        assertEquals("HW1", task2.getName());
        assertNotEquals(task1.getId(), task2.getId()); // Different IDs

        // Verify both tasks exist in the course
        CourseDTO updatedCourse = courseService.getCourseById(course.getId());
        assertEquals(2, updatedCourse.getTasks().size());
    }
}
