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
import com.cpp.project.user_credential.service.UserCredentialService;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

/**
 * State: Change Password
 * <p>
 * Responsibilities:
 * - Allow user to change their password
 * - Validate current password before allowing change
 * - Validate new password strength and confirmation match
 * - Update password via service
 * - Notify mediator on success or cancellation
 */
public class ChangePasswordState implements ScreenState {
    private final UserMediator mediator;
    private final UserCredentialService credentialService;

    private final Form form;
    private final FormField currentPasswordField;
    private final FormField newPasswordField;
    private final FormField confirmPasswordField;
    private final MessagePanel messagePanel;
    private final FormValidator formValidator;

    private UserDTO currentUser;

    public ChangePasswordState(UserMediator mediator, UserCredentialService credentialService) {
        this.mediator = mediator;
        this.credentialService = credentialService;

        currentPasswordField = ComponentFactory.createPasswordField("Current Password");
        newPasswordField = ComponentFactory.createPasswordField("New Password");
        confirmPasswordField = ComponentFactory.createPasswordField("Confirm Password");

        form = new Form()
                .addField(currentPasswordField)
                .addField(newPasswordField)
                .addField(confirmPasswordField);

        messagePanel = new MessagePanel();
        formValidator = new FormValidator(messagePanel);
    }

    @Override
    public void onEnter() {
        // Get current user data
        currentUser = mediator.getCurrentUser();

        // Clear all fields
        currentPasswordField.setValue("");
        newPasswordField.setValue("");
        confirmPasswordField.setValue("");

        form.setFocused(true);
        messagePanel.clear();
    }

    @Override
    public void render(TextGraphics graphics) {
        TerminalSize size = graphics.getSize();

        // Title
        graphics.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
        String title = "=== CHANGE PASSWORD ===";
        graphics.putString(UILayoutConstants.centerX(size, title.length()), UILayoutConstants.TITLE_ROW, title);

        // Instructions
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(UILayoutConstants.LEFT_MARGIN, UILayoutConstants.INSTRUCTIONS_ROW,
                "Tab: Next field | Enter: Save | ESC: Cancel");

        // Password requirements
        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        graphics.putString(UILayoutConstants.FORM_LEFT, UILayoutConstants.FORM_START_ROW, "Password requirements:");
        graphics.setForegroundColor(TextColor.ANSI.CYAN);
        graphics.putString(UILayoutConstants.FORM_LEFT + 2, UILayoutConstants.FORM_START_ROW + 1,
                "- At least 8 characters");
        graphics.putString(UILayoutConstants.FORM_LEFT + 2, UILayoutConstants.FORM_START_ROW + 2,
                "- Contains uppercase and lowercase letters");
        graphics.putString(UILayoutConstants.FORM_LEFT + 2, UILayoutConstants.FORM_START_ROW + 3,
                "- Contains at least one digit");
        graphics.putString(UILayoutConstants.FORM_LEFT + 2, UILayoutConstants.FORM_START_ROW + 4,
                "- Contains at least one special character");

        // Form
        form.render(graphics, UILayoutConstants.FORM_LEFT, UILayoutConstants.FORM_START_ROW + 6);

        // Message panel
        messagePanel.render(graphics, UILayoutConstants.FORM_LEFT, UILayoutConstants.messageRow(size));
    }

    @Override
    public ScreenState handleInput(KeyStroke keyStroke) {
        messagePanel.clear();

        if (keyStroke.getKeyType() == KeyType.Escape) {
            // Cancel password change - return to profile view
            mediator.onCancelPasswordChange();
            return null; // Mediator handles transition
        } else if (keyStroke.getKeyType() == KeyType.Enter) {
            return handleSave();
        } else {
            form.handleInput(keyStroke);
            return this;
        }
    }

    private ScreenState handleSave() {
        String currentPassword = currentPasswordField.getValue();
        String newPassword = newPasswordField.getValue();
        String confirmPassword = confirmPasswordField.getValue();

        // Validation using FormValidator
        if (!formValidator.validateRequired("Current Password", currentPassword)) {
            return this;
        }

        if (!formValidator.validateRequired("New Password", newPassword)) {
            return this;
        }

        if (!formValidator.validateRequired("Confirm Password", confirmPassword)) {
            return this;
        }

        // Check if new password matches confirmation
        if (!newPassword.equals(confirmPassword)) {
            messagePanel.setError("New password and confirmation do not match");
            return this;
        }

        // Check if new password is same as current
        if (currentPassword.equals(newPassword)) {
            messagePanel.setError("New password must be different from current password");
            return this;
        }

        try {
            // Verify current password is correct
            if (!credentialService.verifyPassword(currentUser.getEmail(), currentPassword)) {
                messagePanel.setError("Current password is incorrect");
                return this;
            }

            // Set new password (will validate password strength)
            credentialService.setPassword(currentUser.getEmail(), newPassword);

            // Notify mediator - it will transition to profile view with success message
            mediator.onPasswordChanged();
            return null; // Mediator handles transition
        } catch (Exception e) {
            messagePanel.setError("Password change failed: " + e.getMessage());
            return this;
        }
    }

    @Override
    public String getStateName() {
        return "ChangePassword";
    }
}
