package com.cpp.project.ui.screen;

import com.cpp.project.dashboard.dto.DashboardSummaryDTO;
import com.cpp.project.dashboard.service.DashboardService;
import com.cpp.project.ui.core.ScreenState;
import com.cpp.project.ui.core.StatefulScreen;
import com.cpp.project.ui.mediator.DashboardMediator;
import com.cpp.project.ui.state.dashboard.DashboardViewState;
import com.cpp.project.user.dto.UserDTO;
import com.googlecode.lanterna.screen.Screen;

/**
 * Dashboard Screen implementing Mediator pattern
 * <p>
 * Design Patterns:
 * - Mediator Pattern: Coordinates state interactions and transitions
 * - Facade Pattern: Provides simple interface for states to access dashboard data
 * - State Pattern: Delegates UI behavior to state objects
 * - Composite Pattern: Composes multiple dashboard widgets (Neglect Detector, Impending Doom)
 * <p>
 * Responsibilities:
 * - Owns the data (fetches from dashboard service)
 * - Provides data access to states
 * - Manages screen lifecycle
 */
public class DashboardScreen extends StatefulScreen implements DashboardMediator {
    private final UserDTO currentUser;
    private final DashboardService dashboardService;

    public DashboardScreen(Screen screen, UserDTO currentUser, DashboardService dashboardService) {
        super(screen);
        this.currentUser = currentUser;
        this.dashboardService = dashboardService;

        // Start with dashboard view state
        this.currentState = createDashboardViewState();
        this.currentState.onEnter();
    }

    // ========== Facade Pattern: Simplified Data Access ==========

    @Override
    public DashboardSummaryDTO getDashboardSummary() {
        // Always fetch fresh from service - no caching, no stale data
        return dashboardService.getDashboardSummary(currentUser.getId());
    }

    // ========== Factory Method Pattern: State Creation ==========

    /**
     * Create the dashboard view state
     *
     * @return New DashboardViewState instance
     */
    private ScreenState createDashboardViewState() {
        return new DashboardViewState(this);
    }

    // ========== Inherited from ScreenMediator ==========

    @Override
    public void transitionTo(ScreenState newState) {
        super.transitionToState(newState);
    }

    @Override
    public void closeScreen() {
        super.close();
    }
}
