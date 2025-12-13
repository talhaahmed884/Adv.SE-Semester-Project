package com.cpp.project.uc_18_dashboard;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.course.dto.CourseDTO;
import com.cpp.project.course.dto.CourseTaskDTO;
import com.cpp.project.course.service.CourseService;
import com.cpp.project.dashboard.dto.CourseStudyTimeDTO;
import com.cpp.project.dashboard.dto.DashboardSummaryDTO;
import com.cpp.project.dashboard.service.DashboardService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.timer.dto.TimerDTO;
import com.cpp.project.timer.service.TimerService;
import com.cpp.project.user.dto.SignUpRequestDTO;
import com.cpp.project.user.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UC-18.04: Verify course study time percentage calculation
 */
public class UC_18_04_GetDashboardSummary_Success_PercentageCalculation_Test extends BaseIntegrationTest {
    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private TimerService timerService;

    private UserDTO testUser;

    @BeforeEach
    public void setUp() {
        testUser = authenticationService.signUp(new SignUpRequestDTO(
                "Percentage Test User",
                "percentage.test.uc1804@test.com",
                "Password123!"
        ));
    }

    @Test
    @DisplayName("UC-18.04: Verify study time percentages are correctly calculated")
    public void testPercentageCalculation() throws InterruptedException {
        // Arrange - Create courses with different amounts of timer data
        CourseDTO course1 = courseService.createCourse("CS101", "Course 1", testUser.getId());
        CourseTaskDTO task1 = courseService.addTaskToCourse(
                course1.getId(),
                "Task 1",
                Instant.now().plus(1, ChronoUnit.DAYS),
                "Task 1"
        );

        // Course 1: 200ms of study time
        TimerDTO timer1a = timerService.startTimer(testUser.getId(), task1.getId());
        Thread.sleep(100);
        timerService.stopTimer(timer1a.getId(), testUser.getId());

        TimerDTO timer1b = timerService.startTimer(testUser.getId(), task1.getId());
        Thread.sleep(100);
        timerService.stopTimer(timer1b.getId(), testUser.getId());

        CourseDTO course2 = courseService.createCourse("CS202", "Course 2", testUser.getId());
        CourseTaskDTO task2 = courseService.addTaskToCourse(
                course2.getId(),
                "Task 2",
                Instant.now().plus(1, ChronoUnit.DAYS),
                "Task 2"
        );

        // Course 2: 100ms of study time
        TimerDTO timer2 = timerService.startTimer(testUser.getId(), task2.getId());
        Thread.sleep(100);
        timerService.stopTimer(timer2.getId(), testUser.getId());

        // Course 3: 0ms (neglected)
        CourseDTO course3 = courseService.createCourse("CS303", "Course 3", testUser.getId());

        // Act
        DashboardSummaryDTO result = dashboardService.getDashboardSummary(testUser.getId());

        // Assert
        List<CourseStudyTimeDTO> courseStudyTimes = result.getCourseStudyTimes();
        assertEquals(3, courseStudyTimes.size());

        // Verify total study time
        long totalTime = result.getTotalStudyTimeMillis();
        assertTrue(totalTime >= 300, "Total time should be at least 300ms");

        // Verify percentages sum to approximately 100
        double totalPercentage = courseStudyTimes.stream()
                .mapToDouble(CourseStudyTimeDTO::getPercentage)
                .sum();
        assertEquals(100.0, totalPercentage, 0.1, "Percentages should sum to 100");

        // Verify course 1 has highest percentage (roughly 66%)
        CourseStudyTimeDTO course1Result = courseStudyTimes.stream()
                .filter(c -> c.getCourseCode().equals("CS101"))
                .findFirst()
                .orElse(null);
        assertNotNull(course1Result);
        assertTrue(course1Result.getPercentage() >= 60 && course1Result.getPercentage() <= 70,
                "Course 1 should have roughly 66% of study time");

        // Verify course 2 has medium percentage (roughly 33%)
        CourseStudyTimeDTO course2Result = courseStudyTimes.stream()
                .filter(c -> c.getCourseCode().equals("CS202"))
                .findFirst()
                .orElse(null);
        assertNotNull(course2Result);
        assertTrue(course2Result.getPercentage() >= 30 && course2Result.getPercentage() <= 40,
                "Course 2 should have roughly 33% of study time");

        // Verify course 3 has 0% (neglected)
        CourseStudyTimeDTO course3Result = courseStudyTimes.stream()
                .filter(c -> c.getCourseCode().equals("CS303"))
                .findFirst()
                .orElse(null);
        assertNotNull(course3Result);
        assertEquals(0.0, course3Result.getPercentage(), "Neglected course should have 0%");
        assertEquals(0, course3Result.getTotalMillis(), "Neglected course should have 0ms");
    }
}
