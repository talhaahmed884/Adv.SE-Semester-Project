package com.cpp.project.ui.state.course;

import com.cpp.project.course.service.CourseService;
import com.cpp.project.ui.component.Form;
import com.cpp.project.ui.component.FormField;
import com.cpp.project.ui.component.MessagePanel;
import com.cpp.project.ui.core.ScreenState;
import com.cpp.project.ui.factory.ComponentFactory;
import com.cpp.project.ui.strategy.RequiredFieldStrategy;
import com.cpp.project.user.dto.UserDTO;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;

import java.util.function.Supplier;

/**
 * State 2: Add Course Form
 */
public class AddCourseState implements ScreenState {
    private final Screen screen;
    private final CourseListState previousState;
    private final CourseService courseService;
    private final UserDTO currentUser;
    private final Runnable reloadCourses;
    private final Supplier<CourseListState> createNewListState;
    private final Form form;
    private final FormField codeField;
    private final FormField nameField;
    private final MessagePanel messagePanel;

    public AddCourseState(Screen screen, CourseListState previousState, CourseService courseService,
                          UserDTO currentUser, Runnable reloadCourses,
                          Supplier<CourseListState> createNewListState) {
        this.screen = screen;
        this.previousState = previousState;
        this.courseService = courseService;
        this.currentUser = currentUser;
        this.reloadCourses = reloadCourses;
        this.createNewListState = createNewListState;

        codeField = ComponentFactory.createTextField("Course Code");
        nameField = ComponentFactory.createTextField("Course Name");

        form = new Form()
                .addField(codeField)
                .addField(nameField);

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
        String title = "=== ADD NEW COURSE ===";
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
        String code = codeField.getValue().trim();
        String name = nameField.getValue().trim();

        // Validation
        String codeError = new RequiredFieldStrategy("Course code").validate(code);
        if (codeError != null) {
            messagePanel.setError(codeError);
            return this;
        }

        String nameError = new RequiredFieldStrategy("Course name").validate(name);
        if (nameError != null) {
            messagePanel.setError(nameError);
            return this;
        }

        try {
            courseService.createCourse(code, name, currentUser.getId());
            reloadCourses.run();
            CourseListState newListState = createNewListState.get();
            newListState.setSuccessMessage("Course created successfully!");
            return newListState;
        } catch (Exception e) {
            messagePanel.setError(e.getMessage());
            return this;
        }
    }

    @Override
    public String getStateName() {
        return "AddCourse";
    }
}
