package com.cpp.project.course.entity;

import com.cpp.project.common.entity.TaskStatus;

import java.util.Date;

/**
 * Builder Pattern for CourseTask entity
 */
public class CourseTaskBuilder {
    private String name;
    private String description;
    private Date deadline;
    private int progress = 0;
    private TaskStatus status = TaskStatus.PENDING;
    private Course course;

    public CourseTaskBuilder name(String name) {
        this.name = name;
        return this;
    }

    public CourseTaskBuilder description(String description) {
        this.description = description;
        return this;
    }

    public CourseTaskBuilder deadline(Date deadline) {
        this.deadline = deadline;
        return this;
    }

    public CourseTaskBuilder progress(int progress) {
        this.progress = progress;
        return this;
    }

    public CourseTaskBuilder status(TaskStatus status) {
        this.status = status;
        return this;
    }

    public CourseTaskBuilder course(Course course) {
        this.course = course;
        return this;
    }

    public CourseTask build() {
        CourseTask task = new CourseTask();
        task.setName(name);
        task.setDescription(description);
        task.setDeadline(deadline);
        task.setProgress(progress);
        task.setStatus(status != null ? status : TaskStatus.PENDING);
        task.setCourse(course);
        return task;
    }
}
