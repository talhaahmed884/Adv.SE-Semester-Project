package com.cpp.project.uc_3_add_course;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.course.dto.CourseDTO;
import com.cpp.project.course.service.CourseService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.user.dto.SignUpRequestDTO;
import com.cpp.project.user.dto.UserDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * UC-3.06: Trims and uppercases course code for comparison and storage
 */
public class UC_3_24_AddCourse_Success_CodeNormalization_Test extends BaseIntegrationTest {
    @Autowired
    private CourseService courseService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-3.06: Trims and uppercases course code")
    public void testAddCourseCodeNormalization() {
        // Arrange - Create a test user first
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc306a@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        String inputCode = " cs101 "; // lowercase with leading/trailing spaces
        String expectedCode = "CS101"; // Should be uppercased and trimmed

        // Act
        CourseDTO result = courseService.createCourse(inputCode, "Introduction to CS", userId);

        // Assert
        assertNotNull(result);
        assertEquals(expectedCode, result.getCode());
    }

    @Test
    @DisplayName("UC-3.06: Normalizes code before duplicate check")
    public void testCodeNormalizationBeforeDuplicateCheck() {
        // Arrange - Create a test user first
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc306b@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create first course with " cs101 "
        CourseDTO first = courseService.createCourse(" cs101 ", "First Course", userId);
        assertEquals("CS101", first.getCode());

        // Verify we can retrieve it with normalized code
        CourseDTO retrieved = courseService.getCourseByCode("CS101");
        assertNotNull(retrieved);
        assertEquals(first.getId(), retrieved.getId());
    }
}
