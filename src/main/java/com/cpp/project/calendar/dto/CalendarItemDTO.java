package com.cpp.project.calendar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * DTO representing a calendar item
 * Aggregates tasks from different sources (Course, ToDoList)
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarItemDTO {
    private Instant date;
    private String sourceType; // "COURSE" or "TODO_LIST"
    private String title;
    private String status; // Task status (PENDING, IN_PROGRESS, COMPLETED)
    private String sourceId; // ID of the course or todo list
    private String taskId; // ID of the specific task
}
