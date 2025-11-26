package com.cpp.project.service;

import com.cpp.project.common.ToDoListControllerIntegrationTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Test Suite for ToDoList Management
 * (UC-7: Create To-Do List)
 * (UC-8: Add To-Do List Task)
 * (UC-9: Mark To-Do List Task Complete)
 * Runs all UC-7 through UC-9 test cases in sequence
 */
@Suite
@SuiteDisplayName("Studently - To-Do List Management Test Suite")
@SelectPackages({
        "com.cpp.project.uc_7_create_todolist",
        "com.cpp.project.uc_8_add_todo_list_task",
        "com.cpp.project.uc_9_mark_todo_list_task_complete"
})
@SelectClasses({
        ToDoListControllerIntegrationTest.class
})
public class ToDoListManagementTestSuite {
    // This class is intentionally empty.
    // Test suite annotations handle execution.
}
