package com.cpp.project.service;


import com.cpp.project.common.TimerControllerIntegrationTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Test Suite for Timer Management
 * (UC-15: Start Timer)
 * (UC-16: Stop Timer)
 * (UC-16: Logout Timer)
 * (UC-17: Get Timer summary)
 * Runs all UC-15 through UC-17 test cases in sequence
 */
@Suite
@SuiteDisplayName("Studently - Timer Management Test Suite")
@SelectPackages({
        "com.cpp.project.uc_15_start_timer",
        "com.cpp.project.uc_16_logout_timer",
        "com.cpp.project.uc_16_stop_timer",
        "com.cpp.project.uc_17_timer_summary",
})
@SelectClasses({
        TimerControllerIntegrationTest.class
})
public class TimerManagementTestSuite {
    // This class is intentionally empty.
    // Test suite annotations handle execution.
}
