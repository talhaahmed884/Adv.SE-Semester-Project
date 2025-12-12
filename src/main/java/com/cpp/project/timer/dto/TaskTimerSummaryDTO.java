package com.cpp.project.timer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

/**
 * DTO containing aggregated timer statistics for a task
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskTimerSummaryDTO {
    private UUID courseTaskId;
    private long totalTimeMillis;
    private int sessionCount;
    private List<TimerDTO> sessions;
    private TimerDTO activeSession; // null if no active timer

    public static TaskTimerSummaryDTOBuilder builder() {
        return new TaskTimerSummaryDTOBuilder();
    }
}
