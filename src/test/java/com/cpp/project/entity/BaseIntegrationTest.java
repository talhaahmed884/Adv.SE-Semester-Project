package com.cpp.project.entity;

import com.cpp.project.StudentlyApplication;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base test class that provides database cleanup
 */
@SpringBootTest(classes = StudentlyApplication.class)
@ActiveProfiles("test")
@Transactional
public abstract class BaseIntegrationTest {
    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @AfterEach
    public void cleanupDatabase() {
        // Clean up test data after each test
        jdbcTemplate.execute("DELETE FROM user_credentials WHERE user_id IN (SELECT id FROM users WHERE email LIKE '%test%')");
        jdbcTemplate.execute("DELETE FROM users WHERE email LIKE '%test%'");
    }
}
