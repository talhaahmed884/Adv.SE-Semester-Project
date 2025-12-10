package com.cpp.project.ui.mediator;

import com.cpp.project.ui.core.ScreenMediator;
import com.cpp.project.user.dto.UserDTO;

/**
 * Mediator interface for User profile screen-state interactions
 * <p>
 * Design Patterns:
 * - Mediator Pattern: Coordinates communication between states
 * - Facade Pattern: Simplifies access to user services and data
 */
public interface UserMediator extends ScreenMediator {

    // ========== Facade: Data Access Methods ==========

    /**
     * Get current user data
     *
     * @return Current user DTO
     */
    UserDTO getCurrentUser();

    /**
     * Refresh user data from service
     */
    void refreshUserData();

    // ========== Mediator: Action Notification Methods ==========

    /**
     * Called when user wants to edit their profile
     * Mediator transitions to edit profile state
     */
    void onEditProfile();

    /**
     * Called when profile update is successful
     * Mediator refreshes data and transitions to profile view
     */
    void onProfileUpdated();

    /**
     * Called when user cancels editing
     * Mediator transitions back to profile view
     */
    void onCancelEdit();

    /**
     * Called when user wants to delete their account
     * Mediator handles confirmation and deletion
     */
    void onDeleteAccount();

    /**
     * Called when user wants to return to main menu
     * Mediator closes the user profile screen
     */
    void onReturnToMainMenu();
}
