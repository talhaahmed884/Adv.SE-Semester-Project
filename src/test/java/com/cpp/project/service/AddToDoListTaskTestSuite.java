package com.cpp.project.service;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Test Suite for UC-8 Add ToDo List Task Use Cases
 * Runs all todo list task creation related tests
 */
@Suite
@SuiteDisplayName("Studently - Complete Test Suite")
@SelectPackages({
        "com.cpp.project.uc_8_add_todo_list_task",
})
public class AddToDoListTaskTestSuite {
    // This class remains empty, it is used only as a holder for the above annotations
}
