package com.cpp.project.service;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Test Suite for Course Management (UC-3: Add Course)
 * Runs all test cases for adding courses
 */
@Suite
@SuiteDisplayName("Studently - Complete Test Suite")
@SelectPackages({
        "com.cpp.project.uc_3_add_course",
        "com.cpp.project.uc_3_get_course_progress",
        "com.cpp.project.uc_4_add_course_task",
        "com.cpp.project.uc_5_update_course_progress"
})
public class CourseManagementTestSuite {
    // This class remains empty, it is used only as a holder for the above annotations
}
