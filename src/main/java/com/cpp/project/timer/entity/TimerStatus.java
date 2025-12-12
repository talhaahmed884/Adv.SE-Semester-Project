package com.cpp.project.timer.entity;

/**
 * Enum representing the status of a timer session
 * RUNNING - Timer is actively running
 * STOPPED - Timer session has been stopped
 */
public enum TimerStatus {
    RUNNING("Running"),
    STOPPED("Stopped");

    private final String displayName;

    TimerStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
