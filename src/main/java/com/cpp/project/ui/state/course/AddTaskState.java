package com.cpp.project.ui.state.course;

import com.cpp.project.course.dto.CourseDTO;
import com.cpp.project.course.service.CourseService;
import com.cpp.project.ui.component.DateInput;
import com.cpp.project.ui.component.Form;
import com.cpp.project.ui.component.FormField;
import com.cpp.project.ui.component.MessagePanel;
import com.cpp.project.ui.core.ScreenState;
import com.cpp.project.ui.factory.ComponentFactory;
import com.cpp.project.ui.strategy.RequiredFieldStrategy;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

/**
 * State 4: Add Task Form
 */
public class AddTaskState implements ScreenState {
    private final Screen screen;
    private final CourseDTO course;
    private final CourseDetailsState previousState;
    private final CourseService courseService;
    private final Runnable reloadCourses;
    private final Supplier<List<CourseDTO>> getCourses;
    private final SimpleDateFormat dateFormat;
    private final Supplier<CourseListState> createNewListState;
    private final Form form;
    private final FormField nameField;
    private final FormField descriptionField;
    private final DateInput deadlineInput;
    private final MessagePanel messagePanel;

    public AddTaskState(Screen screen, CourseDTO course, CourseDetailsState previousState,
                        CourseService courseService, Runnable reloadCourses,
                        Supplier<List<CourseDTO>> getCourses, SimpleDateFormat dateFormat,
                        Supplier<CourseListState> createNewListState) {
        this.screen = screen;
        this.course = course;
        this.previousState = previousState;
        this.courseService = courseService;
        this.reloadCourses = reloadCourses;
        this.getCourses = getCourses;
        this.dateFormat = dateFormat;
        this.createNewListState = createNewListState;

        nameField = ComponentFactory.createTextField("Task Name");
        descriptionField = ComponentFactory.createTextField("Description");
        deadlineInput = ComponentFactory.createDateInput("Deadline");

        form = new Form()
                .addField(nameField)
                .addField(descriptionField)
                .addField(deadlineInput);

        messagePanel = new MessagePanel();
    }

    @Override
    public void onEnter() {
        form.setFocused(true);
    }

    @Override
    public void render(TextGraphics graphics) {
        TerminalSize size = screen.getTerminalSize();

        // Title
        graphics.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
        String title = "=== ADD NEW TASK ===";
        graphics.putString((size.getColumns() - title.length()) / 2, 1, title);

        // Instructions
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(3, 3, "Tab: Next field | Enter: Save | ESC: Cancel");

        // Form
        form.render(graphics, 5, 5);

        // Messages
        messagePanel.render(graphics, 5, size.getRows() - 2);
    }

    @Override
    public ScreenState handleInput(KeyStroke keyStroke) {
        messagePanel.clear();

        if (keyStroke.getKeyType() == KeyType.Escape) {
            reloadCourses.run();
            return previousState;
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
        Date deadline = deadlineInput.getDate();

        // Validation
        String nameError = new RequiredFieldStrategy("Task name").validate(name);
        if (nameError != null) {
            messagePanel.setError(nameError);
            return this;
        }

        if (deadline == null) {
            String error = deadlineInput.getErrorMessage();
            messagePanel.setError(error.isEmpty() ? "All date fields are required" : error);
            return this;
        }

        try {
            courseService.addTaskToCourse(course.getId(), name, deadline, description);
            reloadCourses.run();
            CourseDTO updatedCourse = getCourses.get().stream()
                    .filter(c -> c.getId().equals(course.getId()))
                    .findFirst()
                    .orElse(course);
            CourseDetailsState newDetailsState = new CourseDetailsState(
                    screen, updatedCourse, createNewListState.get(), dateFormat, reloadCourses
            );
            newDetailsState.setSuccessMessage("Task added successfully!");
            return newDetailsState;
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
