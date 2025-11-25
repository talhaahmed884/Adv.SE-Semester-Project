package com.cpp.project.service;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Studently - Complete Test Suite")
@SelectPackages({
        "com.cpp.project.UC_1_SignUp",
        "com.cpp.project.UC_2_Login"
})
public class UserManagementTestSuite {
    // Test suite that runs all tests
}
