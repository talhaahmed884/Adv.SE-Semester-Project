package com.cpp.project.service;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Test Suite for ToDoList Management (UC-7: Create To-Do List)
 * Runs all UC-7 test cases in sequence
 */
@Suite
@SuiteDisplayName("Studently - Complete Test Suite")
@SelectPackages({
        "com.cpp.project.uc_7_create_todolist",
        "com.cpp.project.uc_8_add_todo_list_task",
        "com.cpp.project.uc_9_mark_todo_list_task_complete"
})
public class ToDoListManagementTestSuite {
    // This class is intentionally empty.
    // Test suite annotations handle execution.
}
