package com.cpp.project.dashboard.service;

import com.cpp.project.course.dto.CourseDTO;
import com.cpp.project.course.dto.CourseTaskDTO;
import com.cpp.project.course.service.CourseService;
import com.cpp.project.dashboard.dto.CourseStudyTimeDTO;
import com.cpp.project.dashboard.dto.DashboardSummaryDTO;
import com.cpp.project.dashboard.dto.UpcomingTaskDTO;
import com.cpp.project.dashboard.entity.TaskUrgency;
import com.cpp.project.timer.service.TimerService;
import com.cpp.project.todolist.dto.ToDoListDTO;
import com.cpp.project.todolist.dto.ToDoListTaskDTO;
import com.cpp.project.todolist.service.ToDoListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service implementation for Dashboard operations
 * Facade Pattern - Aggregates data from CourseService, TimerService, and ToDoListService
 */
@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {
    private static final Logger logger = LoggerFactory.getLogger(DashboardServiceImpl.class);
    private static final int UPCOMING_DAYS = 7;

    private final CourseService courseService;
    private final TimerService timerService;
    private final ToDoListService todoListService;

    public DashboardServiceImpl(CourseService courseService,
                                TimerService timerService,
                                ToDoListService todoListService) {
        this.courseService = courseService;
        this.timerService = timerService;
        this.todoListService = todoListService;
    }

    @Override
    public DashboardSummaryDTO getDashboardSummary(UUID userId) {
        logger.info("Fetching dashboard summary for user: {}", userId);

        // Aggregate course study times (for Neglect Detector)
        List<CourseStudyTimeDTO> courseStudyTimes = aggregateCourseStudyTimes(userId);

        // Aggregate upcoming tasks (for Impending Doom)
        List<UpcomingTaskDTO> upcomingTasks = aggregateUpcomingTasks(userId);

        // Calculate total study time
        long totalStudyTimeMillis = courseStudyTimes.stream()
                .mapToLong(CourseStudyTimeDTO::getTotalMillis)
                .sum();

        return DashboardSummaryDTO.builder()
                .courseStudyTimes(courseStudyTimes)
                .upcomingTasks(upcomingTasks)
                .totalStudyTimeMillis(totalStudyTimeMillis)
                .build();
    }

    /**
     * Aggregates study time per course with percentage distribution
     * Strategy: Fetch all courses, calculate time per course, compute percentages
     */
    private List<CourseStudyTimeDTO> aggregateCourseStudyTimes(UUID userId) {
        logger.debug("Aggregating course study times for user: {}", userId);

        // Fetch all courses for the user
        List<CourseDTO> courses = courseService.getCoursesByUserId(userId);

        // Calculate time spent per course
        List<CourseStudyTimeDTO> studyTimes = new ArrayList<>();
        long totalTime = 0;

        for (CourseDTO course : courses) {
            long courseTime = 0;

            // Sum time from all tasks in the course
            for (CourseTaskDTO task : course.getTasks()) {
                long taskTime = timerService.getTotalTimeForTask(task.getId());
                courseTime += taskTime;
            }

            totalTime += courseTime;

            studyTimes.add(CourseStudyTimeDTO.builder()
                    .courseId(course.getId())
                    .courseName(course.getName())
                    .courseCode(course.getCode())
                    .totalMillis(courseTime)
                    .percentage(0.0)  // Will be calculated after total is known
                    .build());
        }

        // Calculate percentages
        final long finalTotalTime = totalTime;
        if (finalTotalTime > 0) {
            studyTimes.forEach(dto ->
                    dto.setPercentage((dto.getTotalMillis() * 100.0) / finalTotalTime)
            );
        }

        // Sort by time descending (most studied first)
        studyTimes.sort(Comparator.comparingLong(CourseStudyTimeDTO::getTotalMillis).reversed());

        logger.debug("Found {} courses with total study time: {} ms", studyTimes.size(), totalTime);
        return studyTimes;
    }

    /**
     * Aggregates upcoming tasks from both courses and todo lists
     * Strategy: Fetch all tasks, filter by deadline, determine urgency, sort by deadline
     */
    private List<UpcomingTaskDTO> aggregateUpcomingTasks(UUID userId) {
        logger.debug("Aggregating upcoming tasks for user: {}", userId);

        List<UpcomingTaskDTO> upcomingTasks = new ArrayList<>();
        Instant now = Instant.now();
        Instant sevenDaysLater = now.plus(Duration.ofDays(UPCOMING_DAYS));

        // Add course tasks
        List<CourseDTO> courses = courseService.getCoursesByUserId(userId);
        for (CourseDTO course : courses) {
            for (CourseTaskDTO task : course.getTasks()) {
                if (task.getDeadline() != null &&
                        task.getDeadline().isAfter(now) &&
                        task.getDeadline().isBefore(sevenDaysLater)) {

                    upcomingTasks.add(UpcomingTaskDTO.builder()
                            .taskId(task.getId())
                            .taskName(task.getName())
                            .deadline(task.getDeadline())
                            .urgency(calculateUrgency(task.getDeadline()))
                            .sourceType("COURSE")
                            .sourceName(course.getName())
                            .status(task.getStatus())
                            .build());
                }
            }
        }

        // Add todo list tasks
        List<ToDoListDTO> todoLists = todoListService.getToDoListsByUserId(userId);
        for (ToDoListDTO todoList : todoLists) {
            for (ToDoListTaskDTO task : todoList.getTasks()) {
                if (task.getDeadline() != null &&
                        task.getDeadline().isAfter(now) &&
                        task.getDeadline().isBefore(sevenDaysLater)) {

                    upcomingTasks.add(UpcomingTaskDTO.builder()
                            .taskId(task.getId())
                            .taskName(task.getDescription())
                            .deadline(task.getDeadline())
                            .urgency(calculateUrgency(task.getDeadline()))
                            .sourceType("TODO_LIST")
                            .sourceName(todoList.getName())
                            .status(task.getStatus())
                            .build());
                }
            }
        }

        // Sort by deadline (most urgent first)
        upcomingTasks.sort(Comparator.comparing(UpcomingTaskDTO::getDeadline));

        logger.debug("Found {} upcoming tasks", upcomingTasks.size());
        return upcomingTasks;
    }

    /**
     * Calculates task urgency based on deadline
     * Strategy Pattern - Urgency determination logic
     */
    private TaskUrgency calculateUrgency(Instant deadline) {
        Instant now = Instant.now();
        ZonedDateTime nowDate = now.atZone(ZoneId.systemDefault());
        ZonedDateTime deadlineDate = deadline.atZone(ZoneId.systemDefault());

        // Check if deadline is today
        if (nowDate.toLocalDate().equals(deadlineDate.toLocalDate())) {
            return TaskUrgency.TODAY;
        }

        // Check if deadline is tomorrow
        if (nowDate.toLocalDate().plusDays(1).equals(deadlineDate.toLocalDate())) {
            return TaskUrgency.TOMORROW;
        }

        // Otherwise, it's within this week
        return TaskUrgency.THIS_WEEK;
    }
}
