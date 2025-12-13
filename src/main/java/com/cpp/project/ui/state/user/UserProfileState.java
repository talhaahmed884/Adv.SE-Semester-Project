package com.cpp.project.ui.state.user;

import com.cpp.project.ui.core.ScreenState;
import com.cpp.project.ui.mediator.UserMediator;
import com.cpp.project.ui.util.DateFormatUtils;
import com.cpp.project.ui.util.UILayoutConstants;
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
 * - Display current user information (name, email, member since date)
 * - Provide options to edit profile, change password, or delete account
 * - Handle navigation to edit state, change password state, or back to main menu
 */
public class UserProfileState implements ScreenState {
    private final UserMediator mediator;

    private UserDTO currentUser;
    private int selectedOption = 0; // 0=Edit Profile, 1=Change Password, 2=Delete Account, 3=Back

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
        graphics.putString(UILayoutConstants.centerX(size, title.length()), UILayoutConstants.TITLE_ROW, title);

        // Instructions
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(UILayoutConstants.LEFT_MARGIN, UILayoutConstants.INSTRUCTIONS_ROW,
                "Arrow Keys: Navigate | Enter: Select | ESC: Back to Main Menu");

        // User Information
        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        graphics.putString(UILayoutConstants.FORM_LEFT, UILayoutConstants.INFO_SECTION_ROW,
                "Name:  " + currentUser.getName());
        graphics.putString(UILayoutConstants.FORM_LEFT, UILayoutConstants.INFO_SECTION_ROW + 1,
                "Email: " + currentUser.getEmail());
        graphics.putString(UILayoutConstants.FORM_LEFT, UILayoutConstants.INFO_SECTION_ROW + 2,
                "Member Since: " + DateFormatUtils.formatFullDate(currentUser.getCreatedAt()));

        // Options Menu
        graphics.setForegroundColor(TextColor.ANSI.CYAN);
        graphics.putString(UILayoutConstants.FORM_LEFT, 11, "Options:");

        // Edit Profile option
        if (selectedOption == 0) {
            graphics.setForegroundColor(TextColor.ANSI.GREEN_BRIGHT);
            graphics.putString(UILayoutConstants.FORM_LEFT + 2, 13, "> 1. Edit Profile");
        } else {
            graphics.setForegroundColor(TextColor.ANSI.WHITE);
            graphics.putString(UILayoutConstants.FORM_LEFT + 2, 13, "  1. Edit Profile");
        }

        // Change Password option
        if (selectedOption == 1) {
            graphics.setForegroundColor(TextColor.ANSI.YELLOW_BRIGHT);
            graphics.putString(UILayoutConstants.FORM_LEFT + 2, 14, "> 2. Change Password");
        } else {
            graphics.setForegroundColor(TextColor.ANSI.WHITE);
            graphics.putString(UILayoutConstants.FORM_LEFT + 2, 14, "  2. Change Password");
        }

        // Delete Account option
        if (selectedOption == 2) {
            graphics.setForegroundColor(TextColor.ANSI.RED_BRIGHT);
            graphics.putString(UILayoutConstants.FORM_LEFT + 2, 15, "> 3. Delete Account");
        } else {
            graphics.setForegroundColor(TextColor.ANSI.WHITE);
            graphics.putString(UILayoutConstants.FORM_LEFT + 2, 15, "  3. Delete Account");
        }

        // Back option
        if (selectedOption == 3) {
            graphics.setForegroundColor(TextColor.ANSI.GREEN_BRIGHT);
            graphics.putString(UILayoutConstants.FORM_LEFT + 2, 16, "> 4. Back to Main Menu");
        } else {
            graphics.setForegroundColor(TextColor.ANSI.WHITE);
            graphics.putString(UILayoutConstants.FORM_LEFT + 2, 16, "  4. Back to Main Menu");
        }
    }

    @Override
    public ScreenState handleInput(KeyStroke keyStroke) {
        if (keyStroke.getKeyType() == KeyType.Escape) {
            mediator.onReturnToMainMenu();
            return null; // Mediator handles transition
        } else if (keyStroke.getKeyType() == KeyType.ArrowUp) {
            selectedOption = (selectedOption - 1 + 4) % 4;
            return this;
        } else if (keyStroke.getKeyType() == KeyType.ArrowDown) {
            selectedOption = (selectedOption + 1) % 4;
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
                // Change Password
                mediator.onChangePassword();
                return null; // Mediator handles transition
            }
            case 2 -> {
                // Delete Account
                mediator.onDeleteAccount();
                return null; // Mediator handles transition
            }
            case 3 -> {
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
