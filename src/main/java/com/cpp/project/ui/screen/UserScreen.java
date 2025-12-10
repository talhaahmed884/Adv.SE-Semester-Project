package com.cpp.project.ui.screen;

import com.cpp.project.ui.component.MessagePanel;
import com.cpp.project.ui.core.ScreenState;
import com.cpp.project.ui.core.StatefulScreen;
import com.cpp.project.ui.mediator.UserMediator;
import com.cpp.project.ui.state.user.ChangePasswordState;
import com.cpp.project.ui.state.user.DeleteConfirmationState;
import com.cpp.project.ui.state.user.EditProfileState;
import com.cpp.project.ui.state.user.UserProfileState;
import com.cpp.project.user.dto.UserDTO;
import com.cpp.project.user.service.UserService;
import com.cpp.project.user_credential.service.UserCredentialService;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;

/**
 * User Profile Screen implementing Mediator pattern
 * <p>
 * Design Patterns:
 * - Mediator Pattern: Coordinates all state interactions and transitions
 * - Facade Pattern: Provides simple interface for states to access user data
 * - Factory Method Pattern: Creates states through factory methods
 * - State Pattern: Delegates UI behavior to state objects
 * <p>
 * Responsibilities:
 * - Owns the user data
 * - Coordinates state transitions (Profile View ↔ Edit Profile ↔ Change Password ↔ Delete Confirmation)
 * - Provides data access to states via facade methods
 * - Handles state action notifications
 */
public class UserScreen extends StatefulScreen implements UserMediator {
    private final UserService userService;
    private final UserCredentialService credentialService;
    private final MessagePanel successPanel;
    private UserDTO currentUser;

    public UserScreen(Screen screen, UserDTO currentUser, UserService userService,
                      UserCredentialService credentialService) {
        super(screen);
        this.userService = userService;
        this.credentialService = credentialService;
        this.currentUser = currentUser;
        this.successPanel = new MessagePanel();

        // Start with profile view state
        this.currentState = createUserProfileState();
        this.currentState.onEnter();
    }

    // ========== Facade Pattern: Simplified Data Access ==========

    @Override
    public UserDTO getCurrentUser() {
        return currentUser;
    }

    @Override
    public void refreshUserData() {
        // Refresh user data from service
        try {
            currentUser = userService.getUserById(currentUser.getId());
        } catch (Exception e) {
            // Log error but keep existing data
            System.err.println("Failed to refresh user data: " + e.getMessage());
        }
    }

    // ========== Mediator Pattern: Action Handlers ==========

    @Override
    public void onEditProfile() {
        // Transition to edit profile state
        transitionTo(createEditProfileState());
    }

    @Override
    public void onProfileUpdated() {
        // Refresh data from service
        refreshUserData();

        // Show success message
        successPanel.setSuccess("Profile updated successfully!");

        // Transition back to profile view
        transitionTo(createUserProfileState());
    }

    @Override
    public void onCancelEdit() {
        // User canceled editing - return to profile view
        transitionTo(createUserProfileState());
    }

    @Override
    public void onDeleteAccount() {
        // Create confirmation state
        transitionTo(createDeleteConfirmationState());
    }

    @Override
    public void onChangePassword() {
        // Transition to change password state
        transitionTo(createChangePasswordState());
    }

    @Override
    public void onPasswordChanged() {
        // Show success message
        successPanel.setSuccess("Password changed successfully!");

        // Transition back to profile view
        transitionTo(createUserProfileState());
    }

    @Override
    public void onCancelPasswordChange() {
        // User canceled password change - return to profile view
        transitionTo(createUserProfileState());
    }

    @Override
    public void onReturnToMainMenu() {
        // Close the user profile screen
        close();
    }

    // ========== ScreenMediator: Core Methods ==========

    @Override
    public void transitionTo(ScreenState newState) {
        transitionToState(newState);
    }

    @Override
    public void closeScreen() {
        close();
    }

    // ========== Factory Method Pattern: State Creation ==========

    /**
     * Factory method to create user profile view state
     *
     * @return New user profile state
     */
    private UserProfileState createUserProfileState() {
        return new UserProfileState(this);
    }

    /**
     * Factory method to create edit profile state
     *
     * @return New edit profile state
     */
    private EditProfileState createEditProfileState() {
        return new EditProfileState(this, userService);
    }

    /**
     * Factory method to create delete confirmation state
     *
     * @return New delete confirmation state
     */
    private ScreenState createDeleteConfirmationState() {
        return new DeleteConfirmationState(this, userService);
    }

    /**
     * Factory method to create change password state
     *
     * @return New change password state
     */
    private ChangePasswordState createChangePasswordState() {
        return new ChangePasswordState(this, credentialService);
    }

    // ========== Additional Rendering ==========

    protected void additionalRendering(TextGraphics graphics) {
        // Render success message if present
        TerminalSize size = graphics.getSize();
        if (!successPanel.isEmpty()) {
            successPanel.render(graphics, 5, size.getRows() - 4);
        }
    }
}
