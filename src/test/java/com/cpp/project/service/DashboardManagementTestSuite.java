package com.cpp.project.service;

import com.cpp.project.common.DashboardControllerIntegrationTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Test Suite for Dashboard Management
 * (UC-18: Dashboard Summary)
 * Runs all UC-18 test cases for dashboard functionality
 *
 * Test Coverage:
 * - Dashboard summary with data
 * - Dashboard summary with empty data
 * - Task urgency level calculation
 * - Study time percentage calculation
 * - Sorting order verification
 * - Multiple tasks per course
 * - Completed tasks handling
 * - Tasks without deadlines
 * - Past deadlines exclusion
 * - Mixed sources (courses + todo lists)
 * - REST API endpoint testing
 */
@Suite
@SuiteDisplayName("Studently - Dashboard Management Test Suite")
@SelectPackages({
        "com.cpp.project.uc_18_dashboard"
})
@SelectClasses({
        DashboardControllerIntegrationTest.class
})
public class DashboardManagementTestSuite {
    // This class is intentionally empty.
    // Test suite annotations handle execution.
}
