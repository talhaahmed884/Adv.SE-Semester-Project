package com.cpp.project.common;

import com.cpp.project.common.sanitization.entity.SanitizationStrategyFactory.FieldType;
import com.cpp.project.common.sanitization.service.DataSanitizationService;
import com.cpp.project.entity.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DataSanitizationServiceTest extends BaseIntegrationTest {
    @Autowired
    private DataSanitizationService sanitizationService;

    @BeforeEach
    void setUp() {
        sanitizationService = new DataSanitizationService();
    }

    @Test
    @DisplayName("Email sanitization: should trim and convert to lowercase")
    void testEmailSanitization() {
        // Test trim + lowercase
        assertEquals("test@example.com", sanitizationService.sanitizeEmail("  Test@Example.COM  "));

        // Test already clean email
        assertEquals("clean@email.com", sanitizationService.sanitizeEmail("clean@email.com"));

        // Test only whitespace
        assertEquals("foo@bar.com", sanitizationService.sanitizeEmail("   Foo@Bar.com   "));

        // Test null
        assertNull(sanitizationService.sanitizeEmail(null));
    }

    @Test
    @DisplayName("Name sanitization: should trim and normalize internal whitespace")
    void testNameSanitization() {
        // Test trim
        assertEquals("John Doe", sanitizationService.sanitizeName("  John Doe  "));

        // Test internal whitespace normalization
        assertEquals("Jane Smith", sanitizationService.sanitizeName("Jane    Smith"));

        // Test multiple spaces
        assertEquals("Bob Wilson Jr", sanitizationService.sanitizeName("  Bob   Wilson   Jr  "));

        // Test tabs and mixed whitespace
        assertEquals("Alice Brown", sanitizationService.sanitizeName("Alice\t\tBrown"));

        // Test null
        assertNull(sanitizationService.sanitizeName(null));
    }

    @Test
    @DisplayName("Password sanitization: should NOT modify password")
    void testPasswordSanitization() {
        // Passwords should NOT be sanitized (keep as-is)
        assertEquals("  MyPassword123  ", sanitizationService.sanitizePassword("  MyPassword123  "));
        assertEquals("MixedCase!@#", sanitizationService.sanitizePassword("MixedCase!@#"));

        // Test null
        assertNull(sanitizationService.sanitizePassword(null));
    }

    @Test
    @DisplayName("Generic sanitization using FieldType")
    void testGenericSanitization() {
        // EMAIL type
        assertEquals("user@test.com", sanitizationService.sanitize("  User@Test.COM  ", FieldType.EMAIL));

        // NAME type
        assertEquals("Full Name", sanitizationService.sanitize("  Full  Name  ", FieldType.NAME));

        // PASSWORD type (no-op)
        assertEquals("  Pass123  ", sanitizationService.sanitize("  Pass123  ", FieldType.PASSWORD));

        // GENERIC type (no-op)
        assertEquals("  Generic  ", sanitizationService.sanitize("  Generic  ", FieldType.GENERIC));
    }
}
