package com.cpp.project.ui.state.user;

import com.cpp.project.ui.core.ScreenState;
import com.cpp.project.ui.mediator.UserMediator;
import com.cpp.project.ui.util.UILayoutConstants;
import com.cpp.project.user.service.UserService;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

/**
 * Inner State: Delete Account Confirmation
 * Simple confirmation dialog for account deletion
 */
public class DeleteConfirmationState implements ScreenState {
    private final UserMediator mediator;
    private final UserService userService;
    private int selectedOption = 0; // 0=Cancel, 1=Confirm Delete

    public DeleteConfirmationState(UserMediator mediator, UserService userService) {
        this.mediator = mediator;
        this.userService = userService;
    }

    @Override
    public void onEnter() {
        selectedOption = 0; // Default to Cancel for safety
    }

    @Override
    public void render(TextGraphics graphics) {
        TerminalSize size = graphics.getSize();

        // Title
        graphics.setForegroundColor(TextColor.ANSI.RED_BRIGHT);
        String title = "=== DELETE ACCOUNT ===";
        graphics.putString(UILayoutConstants.centerX(size, title.length()), UILayoutConstants.TITLE_ROW, title);

        // Warning message
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(UILayoutConstants.FORM_LEFT, UILayoutConstants.FORM_START_ROW,
                "WARNING: This action cannot be undone!");
        graphics.putString(UILayoutConstants.FORM_LEFT, UILayoutConstants.FORM_START_ROW + 1,
                "All your data will be permanently deleted.");

        // Instructions
        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        graphics.putString(UILayoutConstants.FORM_LEFT, UILayoutConstants.FORM_START_ROW + 4,
                "Are you sure you want to delete your account?");

        // Options
        if (selectedOption == 0) {
            graphics.setForegroundColor(TextColor.ANSI.GREEN_BRIGHT);
            graphics.putString(UILayoutConstants.FORM_LEFT + 2, UILayoutConstants.FORM_START_ROW + 7, "> Cancel");
        } else {
            graphics.setForegroundColor(TextColor.ANSI.WHITE);
            graphics.putString(UILayoutConstants.FORM_LEFT + 2, UILayoutConstants.FORM_START_ROW + 7, "  Cancel");
        }

        if (selectedOption == 1) {
            graphics.setForegroundColor(TextColor.ANSI.RED_BRIGHT);
            graphics.putString(UILayoutConstants.FORM_LEFT + 2, UILayoutConstants.FORM_START_ROW + 8,
                    "> Yes, Delete My Account");
        } else {
            graphics.setForegroundColor(TextColor.ANSI.WHITE);
            graphics.putString(UILayoutConstants.FORM_LEFT + 2, UILayoutConstants.FORM_START_ROW + 8,
                    "  Yes, Delete My Account");
        }

        // Instructions
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(UILayoutConstants.FORM_LEFT, UILayoutConstants.FORM_START_ROW + 11,
                "Arrow Keys: Navigate | Enter: Confirm | ESC: Cancel");
    }

    @Override
    public ScreenState handleInput(KeyStroke keyStroke) {
        if (keyStroke.getKeyType() == KeyType.Escape) {
            mediator.onCancelEdit();
            return null;
        } else if (keyStroke.getKeyType() == KeyType.ArrowUp ||
                keyStroke.getKeyType() == KeyType.ArrowDown) {
            selectedOption = (selectedOption + 1) % 2;
            return this;
        } else if (keyStroke.getKeyType() == KeyType.Enter) {
            if (selectedOption == 0) {
                // Cancel - return to profile view
                mediator.onCancelEdit();
            } else {
                // Confirm delete
                try {
                    userService.deleteUser(mediator.getCurrentUser().getId());
                    // Close the screen and eventually the application
                    mediator.closeScreen();
                } catch (Exception e) {
                    // Error deleting - return to profile view
                    mediator.onCancelEdit();
                }
            }
            return null;
        }
        return this;
    }

    @Override
    public String getStateName() {
        return "DeleteConfirmation";
    }
}
