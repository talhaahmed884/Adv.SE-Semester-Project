package com.cpp.project.ui.state.user;

import com.cpp.project.ui.component.Form;
import com.cpp.project.ui.component.FormField;
import com.cpp.project.ui.component.MessagePanel;
import com.cpp.project.ui.core.ScreenState;
import com.cpp.project.ui.factory.ComponentFactory;
import com.cpp.project.ui.mediator.UserMediator;
import com.cpp.project.ui.util.FormValidator;
import com.cpp.project.ui.util.UILayoutConstants;
import com.cpp.project.user.dto.UserDTO;
import com.cpp.project.user.service.UserService;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

/**
 * State: Edit Profile
 * <p>
 * Responsibilities:
 * - Allow user to edit their name and email
 * - Validate input before updating
 * - Update user via service
 * - Notify mediator on success or cancellation
 */
public class EditProfileState implements ScreenState {
    private final UserMediator mediator;
    private final UserService userService;

    private final Form form;
    private final FormField nameField;
    private final FormField emailField;
    private final MessagePanel messagePanel;
    private final FormValidator formValidator;

    private UserDTO currentUser;

    public EditProfileState(UserMediator mediator, UserService userService) {
        this.mediator = mediator;
        this.userService = userService;

        nameField = ComponentFactory.createTextField("Name");
        emailField = ComponentFactory.createTextField("Email");

        form = new Form()
                .addField(nameField)
                .addField(emailField);

        messagePanel = new MessagePanel();
        formValidator = new FormValidator(messagePanel);
    }

    @Override
    public void onEnter() {
        // Get current user data and pre-fill form
        currentUser = mediator.getCurrentUser();
        nameField.setValue(currentUser.getName());
        emailField.setValue(currentUser.getEmail());

        form.setFocused(true);
        messagePanel.clear();
    }

    @Override
    public void render(TextGraphics graphics) {
        TerminalSize size = graphics.getSize();

        // Title
        graphics.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
        String title = "=== EDIT PROFILE ===";
        graphics.putString(UILayoutConstants.centerX(size, title.length()), UILayoutConstants.TITLE_ROW, title);

        // Instructions
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(UILayoutConstants.LEFT_MARGIN, UILayoutConstants.INSTRUCTIONS_ROW,
                "Tab: Next field | Enter: Save | ESC: Cancel");

        // Form
        form.render(graphics, UILayoutConstants.FORM_LEFT, UILayoutConstants.FORM_START_ROW);

        // Message panel
        messagePanel.render(graphics, UILayoutConstants.FORM_LEFT, UILayoutConstants.messageRow(size));
    }

    @Override
    public ScreenState handleInput(KeyStroke keyStroke) {
        messagePanel.clear();

        if (keyStroke.getKeyType() == KeyType.Escape) {
            // Cancel editing - return to profile view
            mediator.onCancelEdit();
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
        String email = emailField.getValue().trim();

        // Validation using FormValidator
        if (!formValidator.validateRequired("Name", name)) {
            return this;
        }

        if (!formValidator.validateRequired("Email", email)) {
            return this;
        }

        // Basic email format validation
        if (!email.contains("@") || !email.contains(".")) {
            messagePanel.setError("Invalid email format");
            return this;
        }

        try {
            // Update user via service
            userService.updateUser(currentUser.getId(), name, email);

            // Notify mediator - it will refresh data and transition to profile view
            mediator.onProfileUpdated();
            return null; // Mediator handles transition
        } catch (Exception e) {
            messagePanel.setError("Update failed: " + e.getMessage());
            return this;
        }
    }

    @Override
    public String getStateName() {
        return "EditProfile";
    }
}
