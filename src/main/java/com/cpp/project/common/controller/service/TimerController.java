package com.cpp.project.common.controller.service;

import com.cpp.project.common.controller.dto.ApiSuccessResponse;
import com.cpp.project.timer.dto.StartTimerRequestDTO;
import com.cpp.project.timer.dto.StopTimerRequestDTO;
import com.cpp.project.timer.dto.TaskTimerSummaryDTO;
import com.cpp.project.timer.dto.TimerDTO;
import com.cpp.project.timer.entity.TimerErrorCode;
import com.cpp.project.timer.entity.TimerException;
import com.cpp.project.timer.service.TimerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for Timer operations
 * Follows REST API best practices with ApiSuccessResponse wrapper
 */
@RestController
@RequestMapping("/api/timers")
public class TimerController {
    private final TimerService timerService;

    public TimerController(TimerService timerService) {
        this.timerService = timerService;
    }

    /**
     * Start a new timer
     * POST /api/timers/start
     */
    @PostMapping("/start")
    public ResponseEntity<ApiSuccessResponse<TimerDTO>> startTimer(
            @RequestBody StartTimerRequestDTO request) {

        // Validate request
        if (request.isEmpty()) {
            throw new TimerException(TimerErrorCode.INVALID_USER_ID,
                    "User ID and Course Task ID are required");
        }

        TimerDTO timer = timerService.startTimer(
                request.getUserId(),
                request.getCourseTaskId()
        );

        ApiSuccessResponse<TimerDTO> response = ApiSuccessResponse.<TimerDTO>builder()
                .data(timer)
                .message("Timer started successfully")
                .statusCode(HttpStatus.CREATED.value())
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Stop a timer
     * POST /api/timers/stop
     */
    @PostMapping("/stop")
    public ResponseEntity<ApiSuccessResponse<TimerDTO>> stopTimer(
            @RequestBody StopTimerRequestDTO request) {

        // Validate request
        if (request.isEmpty()) {
            throw new TimerException(TimerErrorCode.INVALID_TASK_ID,
                    "Timer ID and User ID are required");
        }

        TimerDTO timer = timerService.stopTimer(
                request.getTimerId(),
                request.getUserId()
        );

        ApiSuccessResponse<TimerDTO> response = ApiSuccessResponse.<TimerDTO>builder()
                .data(timer)
                .message("Timer stopped successfully")
                .statusCode(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Get all timer sessions for a task
     * GET /api/timers/task/{taskId}
     */
    @GetMapping("/task/{taskId}")
    public ResponseEntity<ApiSuccessResponse<List<TimerDTO>>> getTimersByTaskId(
            @PathVariable UUID taskId) {

        List<TimerDTO> timers = timerService.getTimersByTaskId(taskId);

        ApiSuccessResponse<List<TimerDTO>> response = ApiSuccessResponse.<List<TimerDTO>>builder()
                .data(timers)
                .message("Timers retrieved successfully")
                .statusCode(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Get timer summary for a task (total time, sessions, active timer)
     * GET /api/timers/task/{taskId}/summary
     */
    @GetMapping("/task/{taskId}/summary")
    public ResponseEntity<ApiSuccessResponse<TaskTimerSummaryDTO>> getTimerSummary(
            @PathVariable UUID taskId) {

        TaskTimerSummaryDTO summary = timerService.getTimerSummaryByTaskId(taskId);

        ApiSuccessResponse<TaskTimerSummaryDTO> response =
                ApiSuccessResponse.<TaskTimerSummaryDTO>builder()
                        .data(summary)
                        .message("Timer summary retrieved successfully")
                        .statusCode(HttpStatus.OK.value())
                        .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Get total accumulated time for a task
     * GET /api/timers/task/{taskId}/total
     */
    @GetMapping("/task/{taskId}/total")
    public ResponseEntity<ApiSuccessResponse<Long>> getTotalTimeForTask(
            @PathVariable UUID taskId) {

        long totalTime = timerService.getTotalTimeForTask(taskId);

        ApiSuccessResponse<Long> response = ApiSuccessResponse.<Long>builder()
                .data(totalTime)
                .message("Total time retrieved successfully")
                .statusCode(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Get a specific timer by ID
     * GET /api/timers/{timerId}
     */
    @GetMapping("/{timerId}")
    public ResponseEntity<ApiSuccessResponse<TimerDTO>> getTimerById(
            @PathVariable UUID timerId) {

        TimerDTO timer = timerService.getTimerById(timerId);

        ApiSuccessResponse<TimerDTO> response = ApiSuccessResponse.<TimerDTO>builder()
                .data(timer)
                .message("Timer retrieved successfully")
                .statusCode(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }
}
