package com.cpp.project.uc_5_update_course_progress;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.common.entity.TaskStatus;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * UC-5.4.08: Setting same value is idempotent
 */
public class UC_5_4_08_UpdateProgress_Success_Idempotent_SetSameValue_Test extends BaseIntegrationTest {
    @Autowired
    private CourseService courseService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-5.4.08: Setting same progress value is idempotent")
    public void testUpdateProgressIdempotent() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.progress.uc5408@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a course
        CourseDTO course = courseService.createCourse("CS110", "Cybersecurity", userId);

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

        // Set progress to 55
        CourseTaskDTO firstUpdate = courseService.updateTaskProgress(course.getId(), task.getId(), 55);
        assertEquals(55, firstUpdate.getProgress());
        assertEquals(TaskStatus.IN_PROGRESS, firstUpdate.getStatus());

        // Act - Set progress to 55 again (same value)
        CourseTaskDTO secondUpdate = courseService.updateTaskProgress(course.getId(), task.getId(), 55);

        // Assert - Should remain the same (idempotent)
        assertNotNull(secondUpdate);
        assertEquals(55, secondUpdate.getProgress());
        assertEquals(TaskStatus.IN_PROGRESS, secondUpdate.getStatus());
    }
}
