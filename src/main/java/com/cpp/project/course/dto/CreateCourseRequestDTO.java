package com.cpp.project.course.dto;

import java.util.UUID;

/**
 * Request DTO for creating a course
 */
public class CreateCourseRequestDTO {
    private String code;
    private String name;
    private UUID userId;

    public CreateCourseRequestDTO() {
    }

    public CreateCourseRequestDTO(String code, String name, UUID userId) {
        this.code = code;
        this.name = name;
        this.userId = userId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public boolean isEmpty() {
        return (code == null || code.trim().isEmpty()) &&
                (name == null || name.trim().isEmpty()) &&
                userId == null;
    }
}
