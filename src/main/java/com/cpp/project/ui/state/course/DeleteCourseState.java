package com.cpp.project.ui.state.course;

import com.cpp.project.course.dto.CourseDTO;
import com.cpp.project.course.service.CourseService;
import com.cpp.project.ui.component.MessagePanel;
import com.cpp.project.ui.component.SelectionList;
import com.cpp.project.ui.core.ScreenState;
import com.cpp.project.ui.mediator.CourseMediator;
import com.cpp.project.ui.util.UILayoutConstants;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.util.Arrays;
import java.util.UUID;

/**
 * State: Delete Course Confirmation
 * <p>
 * Responsibilities:
 * - Display course information
 * - Show warning and confirmation options
 * - Delete course via service if confirmed
 * - Notify mediator on success or cancellation
 */
public class DeleteCourseState implements ScreenState {
    private final CourseMediator mediator;
    private final CourseService courseService;
    private final UUID courseId;
    private final SelectionList<String> optionsList;
    private final MessagePanel messagePanel;
    private CourseDTO course; // Cached course data

    private static final String CONFIRM = "Yes, Delete Course";
    private static final String CANCEL = "No, Cancel";

    public DeleteCourseState(CourseMediator mediator, CourseService courseService, UUID courseId) {
        this.mediator = mediator;
        this.courseService = courseService;
        this.courseId = courseId;

        // Create options list with Cancel as default (first item)
        this.optionsList = new SelectionList<>("Select an option", option -> option);
        this.optionsList.setItems(Arrays.asList(CANCEL, CONFIRM));
        this.optionsList.setFocused(true);

        this.messagePanel = new MessagePanel();
    }

    @Override
    public void onEnter() {
        // Fetch fresh course data
        course = mediator.getCourseById(courseId);
    }

    @Override
    public void render(TextGraphics graphics) {
        TerminalSize size = graphics.getSize();

        // Title
        graphics.setForegroundColor(TextColor.ANSI.RED_BRIGHT);
        String title = "=== DELETE COURSE - CONFIRMATION ===";
        graphics.putString(UILayoutConstants.centerX(size, title.length()), UILayoutConstants.TITLE_ROW, title);

        // Warning
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(UILayoutConstants.LEFT_MARGIN, UILayoutConstants.INSTRUCTIONS_ROW, "WARNING: This action cannot be undone!");

        // Course info
        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        graphics.putString(UILayoutConstants.LEFT_MARGIN, UILayoutConstants.CONTENT_START_ROW, "Course Code: " + course.getCode());
        graphics.putString(UILayoutConstants.LEFT_MARGIN, 6, "Course Name: " + course.getName());

        int taskCount = course.getTasks() != null ? course.getTasks().size() : 0;
        graphics.putString(UILayoutConstants.LEFT_MARGIN, 7, "Tasks in Course: " + taskCount);

        if (taskCount > 0) {
            graphics.setForegroundColor(TextColor.ANSI.RED);
            graphics.putString(UILayoutConstants.LEFT_MARGIN, 8, "All tasks will also be deleted!");
        }

        // Instructions
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(UILayoutConstants.LEFT_MARGIN, 10, "Are you sure you want to delete this course?");

        // Options
        optionsList.render(graphics, UILayoutConstants.LEFT_MARGIN, 12);

        // Messages
        messagePanel.render(graphics, UILayoutConstants.LEFT_MARGIN, UILayoutConstants.messageRow(size));
    }

    @Override
    public ScreenState handleInput(KeyStroke keyStroke) {
        messagePanel.clear();

        if (keyStroke.getKeyType() == KeyType.Escape) {
            // Notify mediator to return to course details
            mediator.onViewCourseDetails(courseId);
            return null; // Mediator handles transition
        } else if (keyStroke.getKeyType() == KeyType.Enter) {
            return handleConfirmation();
        } else {
            optionsList.handleInput(keyStroke);
            return this;
        }
    }

    private ScreenState handleConfirmation() {
        String selected = optionsList.getSelectedItem();

        if (CANCEL.equals(selected)) {
            // User cancelled, return to course details
            mediator.onViewCourseDetails(courseId);
            return null; // Mediator handles transition
        } else if (CONFIRM.equals(selected)) {
            // User confirmed, delete the course
            try {
                courseService.deleteCourse(courseId);
                // Notify mediator - it will transition to course list with success message
                mediator.onCourseDeleted();
                return null; // Mediator handles transition
            } catch (Exception e) {
                messagePanel.setError(e.getMessage());
                return this;
            }
        }

        return this;
    }

    @Override
    public String getStateName() {
        return "DeleteCourse";
    }
}
