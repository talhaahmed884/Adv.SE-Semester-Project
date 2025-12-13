package com.cpp.project.ui.state.dashboard;

import com.cpp.project.dashboard.dto.DashboardSummaryDTO;
import com.cpp.project.ui.component.MessagePanel;
import com.cpp.project.ui.component.dashboard.ImpendingDoomWidget;
import com.cpp.project.ui.component.dashboard.NeglectDetectorWidget;
import com.cpp.project.ui.core.ScreenState;
import com.cpp.project.ui.mediator.DashboardMediator;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

/**
 * State for Dashboard View
 * <p>
 * Responsibilities:
 * - Display Neglect Detector widget (course study time distribution)
 * - Display Impending Doom widget (upcoming tasks)
 * - Handle navigation and refresh
 * - No data ownership - fetches fresh from mediator
 * <p>
 * Composite Pattern - Composes two dashboard widgets
 */
public class DashboardViewState implements ScreenState {
    private final DashboardMediator mediator;
    private NeglectDetectorWidget neglectDetectorWidget;
    private ImpendingDoomWidget impendingDoomWidget;
    private final MessagePanel messagePanel;

    public DashboardViewState(DashboardMediator mediator) {
        this.mediator = mediator;
        this.messagePanel = new MessagePanel();
    }

    @Override
    public void onEnter() {
        // Fetch fresh dashboard data when entering this state
        DashboardSummaryDTO summary = mediator.getDashboardSummary();

        // Initialize widgets with fresh data (Composite Pattern)
        neglectDetectorWidget = new NeglectDetectorWidget(summary.getCourseStudyTimes());
        impendingDoomWidget = new ImpendingDoomWidget(summary.getUpcomingTasks());
    }

    @Override
    public void render(TextGraphics graphics) {
        TerminalSize size = graphics.getSize();

        // Title
        graphics.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
        String title = "=== STUDY DASHBOARD ===";
        graphics.putString((size.getColumns() - title.length()) / 2, 1, title);

        // Instructions
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(3, 3, "F5: Refresh | ESC: Back to Main Menu");

        int currentY = 5;

        // Render Neglect Detector Widget (Composite Pattern)
        if (neglectDetectorWidget != null) {
            neglectDetectorWidget.render(graphics, 3, currentY);
            currentY += neglectDetectorWidget.getHeight() + 2;  // Add spacing
        }

        // Render Impending Doom Widget (Composite Pattern)
        if (impendingDoomWidget != null) {
            impendingDoomWidget.render(graphics, 3, currentY);
            currentY += impendingDoomWidget.getHeight() + 2;  // Add spacing
        }

        // Messages
        messagePanel.render(graphics, 3, size.getRows() - 2);
    }

    @Override
    public ScreenState handleInput(KeyStroke keyStroke) {
        messagePanel.clear();

        if (keyStroke.getKeyType() == KeyType.F5) {
            // Refresh dashboard data
            messagePanel.setSuccess("Dashboard refreshed!");
            onEnter();  // Re-fetch data
            return this;
        } else if (keyStroke.getKeyType() == KeyType.Escape) {
            mediator.closeScreen();
            return null;
        }

        return this;
    }

    @Override
    public void onExit() {
        // Cleanup if needed
    }

    @Override
    public String getStateName() {
        return "DashboardView";
    }
}
