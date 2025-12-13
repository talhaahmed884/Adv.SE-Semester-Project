package com.cpp.project.uc_18_dashboard;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.dashboard.dto.DashboardSummaryDTO;
import com.cpp.project.dashboard.service.DashboardService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.user.dto.SignUpRequestDTO;
import com.cpp.project.user.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UC-18.02: Get dashboard summary for user with no data
 */
public class UC_18_02_GetDashboardSummary_Success_EmptyData_Test extends BaseIntegrationTest {
    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private AuthenticationService authenticationService;

    private UserDTO testUser;

    @BeforeEach
    public void setUp() {
        // Create test user with no courses or tasks
        testUser = authenticationService.signUp(new SignUpRequestDTO(
                "New User",
                "new.user.uc1802@test.com",
                "Password123!"
        ));
    }

    @Test
    @DisplayName("UC-18.02: Get dashboard summary for user with no courses or tasks")
    public void testGetDashboardSummaryEmptyData() {
        // Act
        DashboardSummaryDTO result = dashboardService.getDashboardSummary(testUser.getId());

        // Assert - Should return empty lists, not null
        assertNotNull(result);
        assertNotNull(result.getCourseStudyTimes());
        assertNotNull(result.getUpcomingTasks());

        // Verify empty lists
        assertTrue(result.getCourseStudyTimes().isEmpty(), "Should have empty course study times");
        assertTrue(result.getUpcomingTasks().isEmpty(), "Should have empty upcoming tasks");

        // Verify total study time is 0
        assertEquals(0, result.getTotalStudyTimeMillis());
    }
}
