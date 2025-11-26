package com.cpp.project.service;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Studently - Complete Test Suite")
@SelectPackages({
        "com.cpp.project",
})

public class UnifiedTestSuite {
    // This class remains empty, it is used only as a holder for the above annotations
}
