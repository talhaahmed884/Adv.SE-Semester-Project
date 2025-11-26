package com.cpp.project.service;

import com.cpp.project.common.AuthControllerIntegrationTest;
import com.cpp.project.common.DataSanitizationServiceTest;
import com.cpp.project.common.UserControllerIntegrationTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Test Suite for Authentication & User Management
 * (UC-1: Set Password)
 * (UC-1: Sign-Up)
 * (UC-2: Login)
 * (UC-2: Verify Password)
 * Runs all UC-1 through UC-2 test cases in sequence
 */

@Suite
@SuiteDisplayName("Studently - User & Authentication Management Test Suite")
@SelectPackages({
        "com.cpp.project.uc_1_set_password",
        "com.cpp.project.uc_1_signup",
        "com.cpp.project.uc_2_login",
        "com.cpp.project.uc_2_verify_password"
})
@SelectClasses({
        AuthControllerIntegrationTest.class,
        DataSanitizationServiceTest.class,
        UserControllerIntegrationTest.class
})
public class UserManagementTestSuite {
    // Test suite that runs all tests
}
