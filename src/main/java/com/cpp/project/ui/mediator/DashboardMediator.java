package com.cpp.project.ui.mediator;

import com.cpp.project.dashboard.dto.DashboardSummaryDTO;
import com.cpp.project.ui.core.ScreenMediator;

/**
 * Mediator interface for Dashboard screen-state interactions
 * <p>
 * Design Patterns:
 * - Mediator Pattern: Coordinates communication between states
 * - Facade Pattern: Simplifies access to dashboard service
 */
public interface DashboardMediator extends ScreenMediator {

    // ========== Facade: Data Access Methods ==========

    /**
     * Get dashboard summary for current user
     * Includes course study time distribution and upcoming tasks
     *
     * @return Fresh dashboard summary from service
     */
    DashboardSummaryDTO getDashboardSummary();
}
