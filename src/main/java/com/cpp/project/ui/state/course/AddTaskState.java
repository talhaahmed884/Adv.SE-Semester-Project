package com.cpp.project.ui.state.course;

import com.cpp.project.course.service.CourseService;
import com.cpp.project.ui.component.DateInput;
import com.cpp.project.ui.component.Form;
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

import java.time.Instant;
import java.util.UUID;

/**
 * State 4: Add Task Form
 * Responsibilities:
 * - Collect task details from user
 * - Add task to course via service
 * - Notify mediator on success or cancellation
 */
public class AddTaskState implements ScreenState {
    private final CourseMediator mediator;
    private final CourseService courseService;
    private final UUID courseId;
    private final Form form;
    private final FormField nameField;
    private final FormField descriptionField;
    private final DateInput deadlineInput;
    private final MessagePanel messagePanel;
    private final FormValidator validator;

    public AddTaskState(CourseMediator mediator, CourseService courseService, UUID courseId) {
        this.mediator = mediator;
        this.courseService = courseService;
        this.courseId = courseId;

        nameField = ComponentFactory.createTextField("Task Name");
        descriptionField = ComponentFactory.createTextField("Description");
        deadlineInput = ComponentFactory.createDateInput("Deadline");

        form = new Form()
                .addField(nameField)
                .addField(descriptionField)
                .addField(deadlineInput);

        messagePanel = new MessagePanel();
        validator = new FormValidator(messagePanel);
    }

    @Override
    public void onEnter() {
        form.setFocused(true);
    }

    @Override
    public void render(TextGraphics graphics) {
        TerminalSize size = graphics.getSize();

        // Title
        graphics.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
        String title = "=== ADD NEW TASK ===";
        graphics.putString(UILayoutConstants.centerX(size, title.length()), UILayoutConstants.TITLE_ROW, title);

        // Instructions
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(UILayoutConstants.LEFT_MARGIN, UILayoutConstants.INSTRUCTIONS_ROW, "Tab: Next field | Enter: Save | ESC: Cancel");

        // Form
        form.render(graphics, UILayoutConstants.FORM_LEFT, UILayoutConstants.FORM_START_ROW);

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
            form.handleInput(keyStroke);
            return this;
        }
    }

    private ScreenState handleSave() {
        String name = nameField.getValue().trim();
        String description = descriptionField.getValue().trim();
        Instant deadline = deadlineInput.getDate();

        // Validation
        if (!validator.validateRequired("Task name", name)) return this;
        if (!validator.validateDateInput(deadlineInput, "Deadline")) return this;

        try {
            courseService.addTaskToCourse(courseId, name, deadline, description);
            // Notify mediator - it will transition to course details with success message
            mediator.onTaskAdded(courseId);
            return null; // Mediator handles transition
        } catch (Exception e) {
            messagePanel.setError(e.getMessage());
            return this;
        }
    }

    @Override
    public String getStateName() {
        return "AddTask";
    }
}
