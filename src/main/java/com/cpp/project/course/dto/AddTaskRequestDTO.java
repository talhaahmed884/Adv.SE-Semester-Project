package com.cpp.project.course.dto;

import java.util.Date;

/**
 * Request DTO for adding a task to a course
 */
public class AddTaskRequestDTO {
    private String name;
    private Date deadline;
    private String description;

    public AddTaskRequestDTO() {
    }

    public AddTaskRequestDTO(String name, Date deadline, String description) {
        this.name = name;
        this.deadline = deadline;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isEmpty() {
        return (name == null || name.trim().isEmpty()) &&
                deadline == null &&
                (description == null || description.trim().isEmpty());
    }
}
