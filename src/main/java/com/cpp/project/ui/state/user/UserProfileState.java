package com.cpp.project.ui.state.user;

import com.cpp.project.ui.core.ScreenState;
import com.cpp.project.ui.mediator.UserMediator;
import com.cpp.project.user.dto.UserDTO;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

/**
 * State: User Profile View
 * <p>
 * Responsibilities:
 * - Display current user information (name, email)
 * - Provide options to edit profile or delete account
 * - Handle navigation to edit state or back to main menu
 */
public class UserProfileState implements ScreenState {
    private final UserMediator mediator;

    private UserDTO currentUser;
    private int selectedOption = 0; // 0=Edit Profile, 1=Delete Account, 2=Back

    public UserProfileState(UserMediator mediator) {
        this.mediator = mediator;
    }

    @Override
    public void onEnter() {
        // Fetch fresh user data from mediator
        currentUser = mediator.getCurrentUser();
        selectedOption = 0;
    }

    @Override
    public void render(TextGraphics graphics) {
        TerminalSize size = graphics.getSize();

        // Title
        graphics.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
        String title = "=== MY PROFILE ===";
        graphics.putString((size.getColumns() - title.length()) / 2, 1, title);

        // Instructions
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(3, 3, "Arrow Keys: Navigate | Enter: Select | ESC: Back to Main Menu");

        // User Information
        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        graphics.putString(5, 6, "Name:  " + currentUser.getName());
        graphics.putString(5, 7, "Email: " + currentUser.getEmail());

        // Options Menu
        graphics.setForegroundColor(TextColor.ANSI.CYAN);
        graphics.putString(5, 10, "Options:");

        // Edit Profile option
        if (selectedOption == 0) {
            graphics.setForegroundColor(TextColor.ANSI.GREEN_BRIGHT);
            graphics.putString(7, 12, "> 1. Edit Profile");
        } else {
            graphics.setForegroundColor(TextColor.ANSI.WHITE);
            graphics.putString(7, 12, "  1. Edit Profile");
        }

        // Delete Account option
        if (selectedOption == 1) {
            graphics.setForegroundColor(TextColor.ANSI.RED_BRIGHT);
            graphics.putString(7, 13, "> 2. Delete Account");
        } else {
            graphics.setForegroundColor(TextColor.ANSI.WHITE);
            graphics.putString(7, 13, "  2. Delete Account");
        }

        // Back option
        if (selectedOption == 2) {
            graphics.setForegroundColor(TextColor.ANSI.GREEN_BRIGHT);
            graphics.putString(7, 14, "> 3. Back to Main Menu");
        } else {
            graphics.setForegroundColor(TextColor.ANSI.WHITE);
            graphics.putString(7, 14, "  3. Back to Main Menu");
        }
    }

    @Override
    public ScreenState handleInput(KeyStroke keyStroke) {
        if (keyStroke.getKeyType() == KeyType.Escape) {
            mediator.onReturnToMainMenu();
            return null; // Mediator handles transition
        } else if (keyStroke.getKeyType() == KeyType.ArrowUp) {
            selectedOption = (selectedOption - 1 + 3) % 3;
            return this;
        } else if (keyStroke.getKeyType() == KeyType.ArrowDown) {
            selectedOption = (selectedOption + 1) % 3;
            return this;
        } else if (keyStroke.getKeyType() == KeyType.Enter) {
            return handleSelection();
        }

        return this; // Stay in this state
    }

    private ScreenState handleSelection() {
        switch (selectedOption) {
            case 0 -> {
                // Edit Profile
                mediator.onEditProfile();
                return null; // Mediator handles transition
            }
            case 1 -> {
                // Delete Account
                mediator.onDeleteAccount();
                return null; // Mediator handles transition
            }
            case 2 -> {
                // Back to Main Menu
                mediator.onReturnToMainMenu();
                return null; // Mediator handles transition
            }
        }
        return this;
    }

    @Override
    public String getStateName() {
        return "UserProfile";
    }
}
