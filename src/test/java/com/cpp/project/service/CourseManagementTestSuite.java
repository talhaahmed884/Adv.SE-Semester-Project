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
        "com.cpp.project.UC_3_Add_Course",
})
public class CourseManagementTestSuite {
    // This class remains empty, it is used only as a holder for the above annotations
}
