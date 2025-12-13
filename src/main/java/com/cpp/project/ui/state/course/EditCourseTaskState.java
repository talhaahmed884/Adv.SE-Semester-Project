package com.cpp.project.ui.state.course;

import com.cpp.project.course.dto.CourseDTO;
import com.cpp.project.course.dto.CourseTaskDTO;
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
 * State: Edit Task
 * Responsibilities:
 * - Pre-fill form with current task data
 * - Collect updated task details from user
 * - Update task via service
 * - Notify mediator on success or cancellation
 */
public class EditCourseTaskState implements ScreenState {
    private final CourseMediator mediator;
    private final CourseService courseService;
    private final UUID courseId;
    private final UUID taskId;
    private final boolean fromTaskDetails;
    private final Form form;
    private final FormField nameField;
    private final FormField descriptionField;
    private final DateInput deadlineInput;
    private final MessagePanel messagePanel;
    private final FormValidator validator;
    private CourseTaskDTO task; // Cached task data

    public EditCourseTaskState(CourseMediator mediator, CourseService courseService, UUID courseId, UUID taskId, boolean fromTaskDetails) {
        this.mediator = mediator;
        this.courseService = courseService;
        this.courseId = courseId;
        this.taskId = taskId;
        this.fromTaskDetails = fromTaskDetails;

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
        // Fetch fresh task data and pre-fill form
        CourseDTO course = mediator.getCourseById(courseId);
        task = course.getTasks().stream()
                .filter(t -> t.getId().equals(taskId))
                .findFirst()
                .orElse(null);

        if (task != null) {
            nameField.setValue(task.getName());
            descriptionField.setValue(task.getDescription() != null ? task.getDescription() : "");
            deadlineInput.setDate(task.getDeadline());
        }

        form.setFocused(true);
    }

    @Override
    public void render(TextGraphics graphics) {
        TerminalSize size = graphics.getSize();

        // Title
        graphics.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
        String title = "=== EDIT TASK ===";
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

        // Check if anything changed
        if (name.equals(task.getName()) &&
                description.equals(task.getDescription() != null ? task.getDescription() : "") &&
                deadline.equals(task.getDeadline())) {
            messagePanel.setError("No changes detected");
            return this;
        }

        try {
            courseService.updateTask(courseId, taskId, name, deadline, description);
            // Notify mediator - it will transition based on context
            if (fromTaskDetails) {
                mediator.onTaskUpdatedReturnToTaskDetails(courseId, taskId);
            } else {
                mediator.onTaskUpdated(courseId);
            }
            return null; // Mediator handles transition
        } catch (Exception e) {
            messagePanel.setError(e.getMessage());
            return this;
        }
    }

    @Override
    public String getStateName() {
        return "EditTask";
    }
}
