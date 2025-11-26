package com.cpp.project.service;

import com.cpp.project.common.CourseControllerIntegrationTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Test Suite for Course Management
 * (UC-3: Add Course)
 * (UC-3: Get Course Progress)
 * (UC-4: Add Course Task)
 * (UC-5: Update Course Progress)
 * (UC-6: Mark Course Task Complete)
 * Runs all test cases from UC-3 to UC-6
 */
@Suite
@SuiteDisplayName("Studently - Course Management Test Suite")
@SelectPackages({
        "com.cpp.project.uc_3_add_course",
        "com.cpp.project.uc_3_get_course_progress",
        "com.cpp.project.uc_4_add_course_task",
        "com.cpp.project.uc_5_update_course_progress",
        "com.cpp.project.uc_6_mark_course_task_complete"
})
@SelectClasses({
        CourseControllerIntegrationTest.class
})
public class CourseManagementTestSuite {
    // This class remains empty, it is used only as a holder for the above annotations
}
