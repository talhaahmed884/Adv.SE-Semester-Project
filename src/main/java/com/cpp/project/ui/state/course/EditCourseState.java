package com.cpp.project.ui.state.course;

import com.cpp.project.course.dto.CourseDTO;
import com.cpp.project.course.service.CourseService;
import com.cpp.project.ui.component.FormField;
import com.cpp.project.ui.component.MessagePanel;
import com.cpp.project.ui.core.ScreenState;
import com.cpp.project.ui.factory.ComponentFactory;
import com.cpp.project.ui.mediator.CourseMediator;
import com.cpp.project.ui.util.FormValidator;
import com.cpp.project.ui.util.UILayoutConstants;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.util.UUID;

/**
 * State: Edit Course
 * <p>
 * Responsibilities:
 * - Pre-fill form with current course data
 * - Collect updated course name from user
 * - Update course via service
 * - Notify mediator on success or cancellation
 */
public class EditCourseState implements ScreenState {
    private final CourseMediator mediator;
    private final CourseService courseService;
    private final UUID courseId;
    private final FormField nameField;
    private final MessagePanel messagePanel;
    private final FormValidator validator;
    private CourseDTO course; // Cached course data

    public EditCourseState(CourseMediator mediator, CourseService courseService, UUID courseId) {
        this.mediator = mediator;
        this.courseService = courseService;
        this.courseId = courseId;

        this.nameField = ComponentFactory.createTextField("Course Name");
        this.nameField.setFocused(true);
        this.messagePanel = new MessagePanel();
        this.validator = new FormValidator(messagePanel);
    }

    @Override
    public void onEnter() {
        // Fetch fresh course data and pre-fill form
        course = mediator.getCourseById(courseId);
        nameField.setValue(course.getName());
    }

    @Override
    public void render(TextGraphics graphics) {
        TerminalSize size = graphics.getSize();

        // Title
        graphics.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
        String title = "=== EDIT COURSE ===";
        graphics.putString(UILayoutConstants.centerX(size, title.length()), UILayoutConstants.TITLE_ROW, title);

        // Instructions
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(UILayoutConstants.LEFT_MARGIN, UILayoutConstants.INSTRUCTIONS_ROW, "Enter: Save | ESC: Cancel");

        // Course code (read-only)
        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        graphics.putString(UILayoutConstants.FORM_LEFT, UILayoutConstants.FORM_START_ROW, "Course Code: " + course.getCode() + " (cannot be changed)");

        // Name field
        nameField.render(graphics, UILayoutConstants.FORM_LEFT, 7);

        // Messages
        messagePanel.render(graphics, UILayoutConstants.FORM_LEFT, UILayoutConstants.messageRow(size));
    }

    @Override
    public ScreenState handleInput(KeyStroke keyStroke) {
        messagePanel.clear();

        if (keyStroke.getKeyType() == KeyType.Escape) {
            // Notify mediator to return to course details
            mediator.onViewCourseDetails(courseId);
            return null; // Mediator handles transition
        } else if (keyStroke.getKeyType() == KeyType.Enter) {
            return handleSave();
        } else {
            nameField.handleInput(keyStroke);
            return this;
        }
    }

    private ScreenState handleSave() {
        String name = nameField.getValue().trim();

        // Validation
        if (!validator.validateRequired("Course name", name)) return this;

        // Check if anything changed
        if (name.equals(course.getName())) {
            messagePanel.setError("No changes detected");
            return this;
        }

        try {
            courseService.updateCourse(courseId, name);
            // Notify mediator - it will transition to course details with success message
            mediator.onCourseUpdated(courseId);
            return null; // Mediator handles transition
        } catch (Exception e) {
            messagePanel.setError(e.getMessage());
            return this;
        }
    }

    @Override
    public String getStateName() {
        return "EditCourse";
    }
}
