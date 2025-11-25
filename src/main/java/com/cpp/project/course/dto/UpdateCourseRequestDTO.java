package com.cpp.project.course.dto;

/**
 * Request DTO for updating a course
 */
public class UpdateCourseRequestDTO {
    private String name;

    public UpdateCourseRequestDTO() {
    }

    public UpdateCourseRequestDTO(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEmpty() {
        return name == null || name.trim().isEmpty();
    }
}
