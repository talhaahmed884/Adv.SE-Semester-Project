package com.cpp.project.ui.screen;

import com.cpp.project.course.dto.CourseDTO;
import com.cpp.project.course.service.CourseService;
import com.cpp.project.timer.dto.TaskTimerSummaryDTO;
import com.cpp.project.timer.service.TimerService;
import com.cpp.project.ui.core.ScreenState;
import com.cpp.project.ui.core.StatefulScreen;
import com.cpp.project.ui.mediator.CourseMediator;
import com.cpp.project.ui.state.course.*;
import com.cpp.project.user.dto.UserDTO;
import com.googlecode.lanterna.screen.Screen;

import java.util.List;
import java.util.UUID;

/**
 * Course Management Screen implementing Mediator pattern
 * <p>
 * Design Patterns:
 * - Mediator Pattern: Coordinates all state interactions and transitions
 * - Facade Pattern: Provides simple interface for states to access data
 * - Factory Method Pattern: Creates states through factory methods
 * - State Pattern: Delegates UI behavior to state objects
 * <p>
 * Responsibilities:
 * - Owns the data (fetches from service)
 * - Coordinates state transitions
 * - Provides data access to states
 * - Handles state action notifications
 */
public class CourseManagementScreen extends StatefulScreen implements CourseMediator {
    private final UserDTO currentUser;
    private final CourseService courseService;
    private final TimerService timerService;

    public CourseManagementScreen(Screen screen, UserDTO currentUser, CourseService courseService, TimerService timerService) {
        super(screen);
        this.currentUser = currentUser;
        this.courseService = courseService;
        this.timerService = timerService;
        // Start with course list view
        this.currentState = createCourseListState(null);
        this.currentState.onEnter();
    }

    // ========== Facade Pattern: Simplified Data Access ==========

    @Override
    public List<CourseDTO> getAllCourses() {
        // Always fetch fresh from service - no caching, no stale data
        return courseService.getCoursesByUserId(currentUser.getId());
    }

    @Override
    public CourseDTO getCourseById(UUID courseId) {
        // Always fetch fresh from service
        return courseService.getCourseById(courseId);
    }

    // ========== Mediator Pattern: Action Handlers ==========

    @Override
    public void onCourseCreated() {
        // User created a course, return to course list with success message
        transitionTo(createCourseListState("Course created successfully!"));
    }

    @Override
    public void onTaskAdded(UUID courseId) {
        // User added a task, refresh the details view with success message
        transitionTo(createCourseDetailsState(courseId, "Task added successfully!"));
    }

    @Override
    public void onTaskProgressUpdated(UUID courseId, boolean wasCompleted) {
        // User updated task progress, refresh the details view with appropriate message
        String message = wasCompleted ? "Task marked as complete!" : "Progress updated successfully!";
        transitionTo(createCourseDetailsState(courseId, message));
    }

    @Override
    public void onViewCourseDetails(UUID courseId) {
        // User wants to view a course's details
        transitionTo(createCourseDetailsState(courseId, null));
    }

    @Override
    public void onReturnToCourseList() {
        // User pressed ESC, return to course list
        transitionTo(createCourseListState(null));
    }

    @Override
    public void onAddNewCourse() {
        // User pressed F1, show add course form
        transitionTo(createAddCourseState());
    }

    @Override
    public void onAddTaskToCourse(UUID courseId) {
        // User pressed F2, show add task form
        transitionTo(createAddTaskState(courseId));
    }

    @Override
    public void onUpdateTaskProgress(UUID courseId, UUID taskId) {
        // User pressed F3, show update progress form
        transitionTo(createUpdateProgressState(courseId, taskId));
    }

    @Override
    public void onEditCourse(UUID courseId) {
        // User wants to edit course, show edit form
        transitionTo(createEditCourseState(courseId));
    }

    @Override
    public void onCourseUpdated(UUID courseId) {
        // Course was updated, refresh details view with success message
        transitionTo(createCourseDetailsState(courseId, "Course updated successfully!"));
    }

    @Override
    public void onDeleteCourse(UUID courseId) {
        // User wants to delete course, show confirmation dialog
        transitionTo(createDeleteCourseState(courseId));
    }

    @Override
    public void onCourseDeleted() {
        // Course was deleted, return to course list with success message
        transitionTo(createCourseListState("Course deleted successfully!"));
    }

    @Override
    public void onEditTask(UUID courseId, UUID taskId) {
        // User wants to edit task, show edit form
        transitionTo(createEditTaskState(courseId, taskId));
    }

    @Override
    public void onTaskUpdated(UUID courseId) {
        // Task was updated, refresh details view with success message
        transitionTo(createCourseDetailsState(courseId, "Task updated successfully!"));
    }

    @Override
    public void onDeleteTask(UUID courseId, UUID taskId) {
        // User wants to delete task, show confirmation dialog
        transitionTo(createDeleteTaskState(courseId, taskId));
    }

    @Override
    public void onTaskDeleted(UUID courseId) {
        // Task was deleted, refresh details view with success message
        transitionTo(createCourseDetailsState(courseId, "Task deleted successfully!"));
    }

    // ========== ScreenMediator: Core Methods ==========

    @Override
    public void transitionTo(ScreenState newState) {
        transitionToState(newState);
    }

    @Override
    public void closeScreen() {
        close();
    }

    // ========== Timer Feature: Data Access ==========

    @Override
    public TaskTimerSummaryDTO getTimerSummary(UUID courseTaskId) {
        // Always fetch fresh from service - no caching, no stale data
        return timerService.getTimerSummaryByTaskId(courseTaskId);
    }

    // ========== Timer Feature: Action Handlers ==========

    @Override
    public void onViewTimerDetails(UUID courseId, UUID taskId) {
        // User wants to view timer details for a task
        transitionTo(createTimerViewState(courseId, taskId, null));
    }

    @Override
    public void onTimerStarted(UUID courseId, UUID taskId) {
        // Timer started successfully, refresh timer view with success message
        transitionTo(createTimerViewState(courseId, taskId, "Timer started successfully!"));
    }

    @Override
    public void onTimerStopped(UUID courseId, UUID taskId) {
        // Timer stopped successfully, refresh timer view with success message
        transitionTo(createTimerViewState(courseId, taskId, "Timer stopped successfully!"));
    }

    @Override
    public void onTimerError(String errorMessage) {
        // Error occurred in timer operation
        // Current state should handle displaying this error via MessagePanel
        // We'll refresh the current timer view state with the error message
        // This is handled within the TimerViewState itself by catching exceptions
    }

    // ========== Task Details View: Navigation Methods ==========

    @Override
    public void onViewTaskDetails(UUID courseId, UUID taskId) {
        // User wants to view task details
        transitionTo(createTaskDetailsState(courseId, taskId, null));
    }

    // ========== Task Details View: Context-Aware Entry Points ==========

    @Override
    public void onUpdateTaskProgressFromTaskDetails(UUID courseId, UUID taskId) {
        // User wants to update task progress from task details view
        transitionTo(createUpdateProgressState(courseId, taskId, true));
    }

    @Override
    public void onEditTaskFromTaskDetails(UUID courseId, UUID taskId) {
        // User wants to edit task from task details view
        transitionTo(createEditTaskState(courseId, taskId, true));
    }

    @Override
    public void onDeleteTaskFromTaskDetails(UUID courseId, UUID taskId) {
        // User wants to delete task from task details view
        transitionTo(createDeleteTaskState(courseId, taskId, true));
    }

    // ========== Task Details View: Callback Methods ==========

    @Override
    public void onTaskUpdatedReturnToTaskDetails(UUID courseId, UUID taskId) {
        // Task was updated from task details view, return to task details with success message
        transitionTo(createTaskDetailsState(courseId, taskId, "Task updated successfully!"));
    }

    @Override
    public void onTaskProgressUpdatedReturnToTaskDetails(UUID courseId, UUID taskId, boolean wasCompleted) {
        // Task progress updated from task details view, return to task details with success message
        String message = wasCompleted ? "Task marked as complete!" : "Progress updated successfully!";
        transitionTo(createTaskDetailsState(courseId, taskId, message));
    }

    // ========== Factory Method Pattern: State Creation ==========

    /**
     * Factory method to create course list state
     *
     * @param message Optional success message to display
     * @return New course list state
     */
    private CourseListState createCourseListState(String message) {
        return new CourseListState(this, message);
    }

    /**
     * Factory method to create course details state
     *
     * @param courseId The course to display
     * @param message  Optional success message to display
     * @return New course details state
     */
    private CourseDetailsState createCourseDetailsState(UUID courseId, String message) {
        return new CourseDetailsState(this, courseId, message);
    }

    /**
     * Factory method to create add course state
     *
     * @return New add course state
     */
    private AddCourseState createAddCourseState() {
        return new AddCourseState(this, currentUser, courseService);
    }

    /**
     * Factory method to create add task state
     *
     * @param courseId The course to add task to
     * @return New add task state
     */
    private AddTaskState createAddTaskState(UUID courseId) {
        return new AddTaskState(this, courseService, courseId);
    }

    /**
     * Factory method to create task details state
     *
     * @param courseId The course containing the task
     * @param taskId   The task to display
     * @param message  Optional success message to display
     * @return New task details state
     */
    private TaskDetailsState createTaskDetailsState(UUID courseId, UUID taskId, String message) {
        return new TaskDetailsState(this, courseId, taskId, message);
    }

    /**
     * Factory method to create update progress state
     *
     * @param courseId The course containing the task
     * @param taskId   The task to update progress for
     * @return New update progress state
     */
    private UpdateProgressState createUpdateProgressState(UUID courseId, UUID taskId) {
        return createUpdateProgressState(courseId, taskId, false);
    }

    /**
     * Factory method to create update progress state with context
     *
     * @param courseId        The course containing the task
     * @param taskId          The task to update progress for
     * @param fromTaskDetails Whether called from task details view
     * @return New update progress state
     */
    private UpdateProgressState createUpdateProgressState(UUID courseId, UUID taskId, boolean fromTaskDetails) {
        return new UpdateProgressState(this, courseService, courseId, taskId, fromTaskDetails);
    }

    /**
     * Factory method to create edit course state
     *
     * @param courseId The course to edit
     * @return New edit course state
     */
    private EditCourseState createEditCourseState(UUID courseId) {
        return new EditCourseState(this, courseService, courseId);
    }

    /**
     * Factory method to create delete course state
     *
     * @param courseId The course to delete
     * @return New delete course confirmation state
     */
    private DeleteCourseState createDeleteCourseState(UUID courseId) {
        return new DeleteCourseState(this, courseService, courseId);
    }

    /**
     * Factory method to create edit task state
     *
     * @param courseId The course containing the task
     * @param taskId   The task to edit
     * @return New edit task state
     */
    private EditCourseTaskState createEditTaskState(UUID courseId, UUID taskId) {
        return createEditTaskState(courseId, taskId, false);
    }

    /**
     * Factory method to create edit task state with context
     *
     * @param courseId        The course containing the task
     * @param taskId          The task to edit
     * @param fromTaskDetails Whether called from task details view
     * @return New edit task state
     */
    private EditCourseTaskState createEditTaskState(UUID courseId, UUID taskId, boolean fromTaskDetails) {
        return new EditCourseTaskState(this, courseService, courseId, taskId, fromTaskDetails);
    }

    /**
     * Factory method to create delete task state
     *
     * @param courseId The course containing the task
     * @param taskId   The task to delete
     * @return New delete task confirmation state
     */
    private DeleteCourseTaskState createDeleteTaskState(UUID courseId, UUID taskId) {
        return createDeleteTaskState(courseId, taskId, false);
    }

    /**
     * Factory method to create delete task state with context
     *
     * @param courseId        The course containing the task
     * @param taskId          The task to delete
     * @param fromTaskDetails Whether called from task details view
     * @return New delete task confirmation state
     */
    private DeleteCourseTaskState createDeleteTaskState(UUID courseId, UUID taskId, boolean fromTaskDetails) {
        return new DeleteCourseTaskState(this, courseService, courseId, taskId, fromTaskDetails);
    }

    /**
     * Factory method to create timer view state
     *
     * @param courseId The course containing the task
     * @param taskId   The task to view timer for
     * @param message  Optional success/error message to display
     * @return New timer view state
     */
    private TimerViewState createTimerViewState(UUID courseId, UUID taskId, String message) {
        return new TimerViewState(this, timerService, currentUser, courseId, taskId, message);
    }
}
