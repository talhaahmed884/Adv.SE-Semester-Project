package com.cpp.project.timer.dto;

import java.util.List;
import java.util.UUID;

/**
 * Builder Pattern for TaskTimerSummaryDTO
 */
public class TaskTimerSummaryDTOBuilder {
    private UUID courseTaskId;
    private long totalTimeMillis;
    private int sessionCount;
    private List<TimerDTO> sessions;
    private TimerDTO activeSession;

    public TaskTimerSummaryDTOBuilder courseTaskId(UUID courseTaskId) {
        this.courseTaskId = courseTaskId;
        return this;
    }

    public TaskTimerSummaryDTOBuilder totalTimeMillis(long totalTimeMillis) {
        this.totalTimeMillis = totalTimeMillis;
        return this;
    }

    public TaskTimerSummaryDTOBuilder sessionCount(int sessionCount) {
        this.sessionCount = sessionCount;
        return this;
    }

    public TaskTimerSummaryDTOBuilder sessions(List<TimerDTO> sessions) {
        this.sessions = sessions;
        return this;
    }

    public TaskTimerSummaryDTOBuilder activeSession(TimerDTO activeSession) {
        this.activeSession = activeSession;
        return this;
    }

    public TaskTimerSummaryDTO build() {
        return new TaskTimerSummaryDTO(courseTaskId, totalTimeMillis, sessionCount, sessions, activeSession);
    }
}
