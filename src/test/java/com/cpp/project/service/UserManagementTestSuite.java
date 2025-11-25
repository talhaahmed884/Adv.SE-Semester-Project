package com.cpp.project.service;

import com.cpp.project.common.AuthControllerIntegrationTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Studently - Complete Test Suite")
@SelectPackages({
        "com.cpp.project.uc_1_set_password",
        "com.cpp.project.uc_1_signup",
        "com.cpp.project.uc_2_login",
        "com.cpp.project.uc_2_verify_password"
})
@SelectClasses({
        AuthControllerIntegrationTest.class
})
public class UserManagementTestSuite {
    // Test suite that runs all tests
}
