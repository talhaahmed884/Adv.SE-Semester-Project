package com.cpp.project.course.dto;

/**
 * Request DTO for updating task progress
 */
public class UpdateTaskProgressRequestDTO {
    private int progress;

    public UpdateTaskProgressRequestDTO() {
    }

    public UpdateTaskProgressRequestDTO(int progress) {
        this.progress = progress;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
