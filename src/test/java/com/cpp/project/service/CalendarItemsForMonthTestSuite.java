package com.cpp.project.service;

import com.cpp.project.common.CalendarControllerIntegrationTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Test Suite for UC-10 Calendar Items for Month Use Cases
 * Runs all calendar items for month related tests
 */
@SuiteDisplayName("Studently - Calendar View Tasks Test Suite")
@Suite
@SelectPackages({
        "com.cpp.project.uc_10_calendar_items_for_month"
})
@SelectClasses({
        CalendarControllerIntegrationTest.class
})
public class CalendarItemsForMonthTestSuite {
    // This class remains empty, it is used only as a holder for the above annotations
}
