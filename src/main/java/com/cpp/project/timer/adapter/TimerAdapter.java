package com.cpp.project.timer.adapter;

import com.cpp.project.timer.dto.TimerDTO;
import com.cpp.project.timer.entity.TaskTimer;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Adapter Pattern - Converts between TaskTimer entities and DTOs
 */
public class TimerAdapter {
    /**
     * Convert TaskTimer entity to DTO
     *
     * @param timer The entity to convert
     * @return TimerDTO or null if input is null
     */
    public static TimerDTO toDTO(TaskTimer timer) {
        if (timer == null) {
            return null;
        }

        return TimerDTO.builder()
                .id(timer.getId())
                .userId(timer.getUserId())
                .courseTaskId(timer.getCourseTaskId())
                .startTime(timer.getStartTime())
                .endTime(timer.getEndTime())
                .durationMillis(timer.getDurationMillis())
                .status(timer.getStatus())
                .createdAt(timer.getCreatedAt())
                .updatedAt(timer.getUpdatedAt())
                .build();
    }

    /**
     * Convert list of TaskTimer entities to DTOs
     *
     * @param timers The list of entities to convert
     * @return List of TimerDTOs or empty list if input is null
     */
    public static List<TimerDTO> toDTOList(List<TaskTimer> timers) {
        if (timers == null) {
            return List.of();
        }

        return timers.stream()
                .map(TimerAdapter::toDTO)
                .collect(Collectors.toList());
    }
}
