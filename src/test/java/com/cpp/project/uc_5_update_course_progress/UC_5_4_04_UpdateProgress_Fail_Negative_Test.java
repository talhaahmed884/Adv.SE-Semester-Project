package com.cpp.project.uc_5_update_course_progress;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.course.dto.CourseDTO;
import com.cpp.project.course.dto.CourseTaskDTO;
import com.cpp.project.course.entity.CourseErrorCode;
import com.cpp.project.course.entity.CourseException;
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
 * UC-5.4.04: Rejects values below 0
 */
public class UC_5_4_04_UpdateProgress_Fail_Negative_Test extends BaseIntegrationTest {
    @Autowired
    private CourseService courseService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-5.4.04: Rejects negative progress value (-1)")
    public void testUpdateProgressNegative() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.progress.uc5404@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a course
        CourseDTO course = courseService.createCourse("CS106", "Database Systems", userId);

        // Create a future deadline
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date futureDeadline = calendar.getTime();

        // Add a task
        CourseTaskDTO task = courseService.addTaskToCourse(
                course.getId(),
                "Assignment 1",
                futureDeadline,
                "Complete the assignment"
        );

        // Act & Assert - Update progress to -1 should fail
        CourseException exception = assertThrows(CourseException.class, () -> {
            courseService.updateTaskProgress(course.getId(), task.getId(), -1);
        });

        assertEquals(CourseErrorCode.INVALID_TASK_PROGRESS.getCode(), exception.getCode());
        assertTrue(exception.getMessage().toLowerCase().contains("progress") ||
                exception.getMessage().toLowerCase().contains("0") ||
                exception.getMessage().toLowerCase().contains("100"));
    }
}
