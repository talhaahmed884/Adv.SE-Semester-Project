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
        // Order matters due to foreign key constraints

        // Clean up course-related data
        try {
            jdbcTemplate.execute("DELETE FROM course_tasks");
            jdbcTemplate.execute("DELETE FROM courses");
        } catch (Exception e) {
            // Tables might not exist yet, ignore
        }

        // Clean up todolist-related data
        try {
            jdbcTemplate.execute("DELETE FROM todo_list_tasks");
            jdbcTemplate.execute("DELETE FROM todo_lists");
        } catch (Exception e) {
            // Tables might not exist yet, ignore
        }

        try {
            jdbcTemplate.execute("DELETE FROM user_credentials WHERE user_id IN (SELECT id FROM users WHERE email LIKE '%test%')");
        } catch (Exception e) {
            // Tables might not exist yet, ignore
        }

        try {
            // Clean up user-related data
            jdbcTemplate.execute("DELETE FROM users WHERE email LIKE '%test%'");
        } catch (Exception e) {
            // Tables might not exist yet, ignore
        }
    }
}
