package com.cpp.project.course.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Builder Pattern for Course entity
 */
public class CourseBuilder {
    private String code;
    private String name;
    private UUID userId;
    private List<CourseTask> tasks = new ArrayList<>();

    public CourseBuilder code(String code) {
        this.code = code;
        return this;
    }

    public CourseBuilder name(String name) {
        this.name = name;
        return this;
    }

    public CourseBuilder userId(UUID userId) {
        this.userId = userId;
        return this;
    }

    public CourseBuilder tasks(List<CourseTask> tasks) {
        this.tasks = tasks;
        return this;
    }

    public Course build() {
        Course course = new Course();
        course.setCode(code);
        course.setName(name);
        course.setUserId(userId);
        course.setTasks(tasks != null ? tasks : new ArrayList<>());
        return course;
    }
}
