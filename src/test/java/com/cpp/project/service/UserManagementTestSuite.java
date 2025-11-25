package com.cpp.project.service;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Studently - Complete Test Suite")
@SelectPackages({
        "com.cpp.project.uc_1_signup",
        "com.cpp.project.uc_2_login",
        "com.cpp.project.common"
})
public class UserManagementTestSuite {
    // Test suite that runs all tests
}
