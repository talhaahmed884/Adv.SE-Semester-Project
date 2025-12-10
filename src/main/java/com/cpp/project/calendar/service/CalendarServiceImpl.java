package com.cpp.project.calendar.service;

import com.cpp.project.calendar.dto.CalendarItemDTO;
import com.cpp.project.calendar.entity.CalendarErrorCode;
import com.cpp.project.calendar.entity.CalendarException;
import com.cpp.project.calendar.validation.CalendarDateValidator;
import com.cpp.project.common.validation.entity.ValidationResult;
import com.cpp.project.course.entity.Course;
import com.cpp.project.course.entity.CourseTask;
import com.cpp.project.course.repository.CourseRepository;
import com.cpp.project.todolist.entity.ToDoList;
import com.cpp.project.todolist.entity.ToDoListTask;
import com.cpp.project.todolist.repository.ToDoListRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of CalendarService
 * Aggregates tasks from Course and ToDoList domains
 */
@Service
@Transactional
public class CalendarServiceImpl implements CalendarService {
    private static final Logger logger = LoggerFactory.getLogger(CalendarServiceImpl.class);

    private final CourseRepository courseRepository;
    private final ToDoListRepository todoListRepository;
    private final CalendarDateValidator dateValidator;

    public CalendarServiceImpl(
            CourseRepository courseRepository,
            ToDoListRepository todoListRepository) {
        this.courseRepository = courseRepository;
        this.todoListRepository = todoListRepository;
        this.dateValidator = new CalendarDateValidator();
    }

    @Override
    public List<CalendarItemDTO> getItemsForMonth(int year, int month, UUID userId, String timezoneId) {
        logger.info("Getting calendar items for user {} for {}/{} in timezone {}", userId, month, year, timezoneId);

        // Validate date parameters
        ValidationResult dateValidation = dateValidator.validate(
                new CalendarDateValidator.DateParams(year, month)
        );
        if (!dateValidation.isValid()) {
            throw new CalendarException(CalendarErrorCode.INVALID_DATE, dateValidation.getFirstError());
        }

        // Parse and validate timezone
        ZoneId userZone;
        try {
            userZone = ZoneId.of(timezoneId);
        } catch (Exception e) {
            logger.error("Invalid timezone ID: {}", timezoneId, e);
            throw new CalendarException(CalendarErrorCode.INVALID_DATE, "Invalid timezone: " + timezoneId);
        }

        // Calculate date range for the month in user's timezone, then convert to UTC
        // The year/month parameters represent the user's timezone month
        ZonedDateTime startLocal = ZonedDateTime.of(year, month, 1, 0, 0, 0, 0, userZone);
        Instant startDate = startLocal.toInstant(); // Converts to UTC for DB query

        ZonedDateTime endLocal = startLocal.plusMonths(1).minusNanos(1);
        Instant endDate = endLocal.toInstant(); // Converts to UTC for DB query

        List<CalendarItemDTO> items = new ArrayList<>();

        // Aggregate tasks from Courses
        List<Course> courses = courseRepository.findByUserId(userId);
        for (Course course : courses) {
            for (CourseTask task : course.getTasks()) {
                if (task.getDeadline() != null &&
                        !task.getDeadline().isBefore(startDate) &&
                        !task.getDeadline().isAfter(endDate)) {

                    CalendarItemDTO item = CalendarItemDTO.builder()
                            .date(task.getDeadline())
                            .sourceType("COURSE")
                            .title(task.getName())
                            .status(task.getStatus().name())
                            .sourceId(course.getId().toString())
                            .taskId(task.getId().toString())
                            .build();

                    items.add(item);
                }
            }
        }

        // Aggregate tasks from ToDoLists
        List<ToDoList> todoLists = todoListRepository.findByUserId(userId);
        for (ToDoList todoList : todoLists) {
            for (ToDoListTask task : todoList.getTasks()) {
                if (task.getDeadline() != null &&
                        !task.getDeadline().isBefore(startDate) &&
                        !task.getDeadline().isAfter(endDate)) {

                    CalendarItemDTO item = CalendarItemDTO.builder()
                            .date(task.getDeadline())
                            .sourceType("TODO_LIST")
                            .title(task.getDescription())
                            .status(task.getStatus().name())
                            .sourceId(todoList.getId().toString())
                            .taskId(task.getId().toString())
                            .build();

                    items.add(item);
                }
            }
        }

        // Sort by date
        items.sort(Comparator.comparing(CalendarItemDTO::getDate));

        logger.info("Found {} calendar items for user {} for {}/{}",
                items.size(), userId, month, year);

        return items;
    }
}
