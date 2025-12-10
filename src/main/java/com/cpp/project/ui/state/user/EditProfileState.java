package com.cpp.project.ui.state.user;

import com.cpp.project.ui.component.Form;
import com.cpp.project.ui.component.FormField;
import com.cpp.project.ui.component.MessagePanel;
import com.cpp.project.ui.core.ScreenState;
import com.cpp.project.ui.factory.ComponentFactory;
import com.cpp.project.ui.mediator.UserMediator;
import com.cpp.project.ui.strategy.RequiredFieldStrategy;
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
        graphics.putString((size.getColumns() - title.length()) / 2, 1, title);

        // Instructions
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(3, 3, "Tab: Next field | Enter: Save | ESC: Cancel");

        // Form
        form.render(graphics, 5, 5);

        // Message panel
        messagePanel.render(graphics, 5, size.getRows() - 2);
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

        // Validation
        String nameError = new RequiredFieldStrategy("Name").validate(name);
        if (nameError != null) {
            messagePanel.setError(nameError);
            return this;
        }

        String emailError = new RequiredFieldStrategy("Email").validate(email);
        if (emailError != null) {
            messagePanel.setError(emailError);
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
