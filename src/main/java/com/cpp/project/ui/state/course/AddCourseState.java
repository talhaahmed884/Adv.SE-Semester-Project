package com.cpp.project.ui.state.course;

import com.cpp.project.course.service.CourseService;
import com.cpp.project.ui.component.Form;
import com.cpp.project.ui.component.FormField;
import com.cpp.project.ui.component.MessagePanel;
import com.cpp.project.ui.core.ScreenState;
import com.cpp.project.ui.factory.ComponentFactory;
import com.cpp.project.ui.mediator.CourseMediator;
import com.cpp.project.ui.strategy.RequiredFieldStrategy;
import com.cpp.project.user.dto.UserDTO;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

/**
 * State 2: Add Course Form
 *
 * Responsibilities:
 * - Collect course details from user
 * - Create new course via service
 * - Notify mediator on success or cancellation
 */
public class AddCourseState implements ScreenState {
    private final CourseMediator mediator;
    private final CourseService courseService;
    private final UserDTO currentUser;
    private final Form form;
    private final FormField codeField;
    private final FormField nameField;
    private final MessagePanel messagePanel;

    public AddCourseState(CourseMediator mediator, UserDTO currentUser, CourseService courseService) {
        this.mediator = mediator;
        this.courseService = courseService;
        this.currentUser = currentUser;

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
        TerminalSize size = graphics.getSize();

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
            // Notify mediator to return to course list
            mediator.onReturnToCourseList();
            return null; // Mediator handles transition
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
            // Notify mediator - it will transition to course list with success message
            mediator.onCourseCreated();
            return null; // Mediator handles transition
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
