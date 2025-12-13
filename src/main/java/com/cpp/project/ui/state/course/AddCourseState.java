package com.cpp.project.ui.state.course;

import com.cpp.project.course.service.CourseService;
import com.cpp.project.ui.component.Form;
import com.cpp.project.ui.component.FormField;
import com.cpp.project.ui.component.MessagePanel;
import com.cpp.project.ui.core.ScreenState;
import com.cpp.project.ui.factory.ComponentFactory;
import com.cpp.project.ui.mediator.CourseMediator;
import com.cpp.project.ui.util.FormValidator;
import com.cpp.project.ui.util.UILayoutConstants;
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
    private final FormValidator validator;

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
        String title = "=== ADD NEW COURSE ===";
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
        if (!validator.validateRequired("Course code", code)) return this;
        if (!validator.validateRequired("Course name", name)) return this;

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
