package com.cpp.project.timer.service;

import com.cpp.project.common.validation.entity.ValidationResult;
import com.cpp.project.common.validation.service.TimerValidationService;
import com.cpp.project.course.repository.CourseRepository;
import com.cpp.project.timer.adapter.TimerAdapter;
import com.cpp.project.timer.dto.TaskTimerSummaryDTO;
import com.cpp.project.timer.dto.TimerDTO;
import com.cpp.project.timer.entity.TaskTimer;
import com.cpp.project.timer.entity.TimerErrorCode;
import com.cpp.project.timer.entity.TimerException;
import com.cpp.project.timer.entity.TimerStatus;
import com.cpp.project.timer.repository.TimerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service implementation for Timer operations
 * Follows data pipeline: Sanitize → Validate → Process → Repository → Adapt
 */
@Service
@Transactional
public class TimerServiceImpl implements TimerService {
    private static final Logger logger = LoggerFactory.getLogger(TimerServiceImpl.class);

    private final TimerRepository timerRepository;
    private final CourseRepository courseRepository;
    private final TimerValidationService validationService;

    public TimerServiceImpl(TimerRepository timerRepository,
                            CourseRepository courseRepository,
                            TimerValidationService validationService) {
        this.timerRepository = timerRepository;
        this.courseRepository = courseRepository;
        this.validationService = validationService;
    }

    @Override
    public TimerDTO startTimer(UUID userId, UUID courseTaskId) {
        logger.info("Starting timer for user: {} and task: {}", userId, courseTaskId);

        try {
            // Step 1: Validate input (UUIDs are already type-safe, no sanitization needed)
            ValidationResult userValidation = validationService.validateUserId(userId);
            if (!userValidation.isValid()) {
                throw new TimerException(TimerErrorCode.INVALID_USER_ID, userValidation.getFirstError());
            }

            ValidationResult taskValidation = validationService.validateCourseTaskId(courseTaskId);
            if (!taskValidation.isValid()) {
                throw new TimerException(TimerErrorCode.INVALID_TASK_ID, taskValidation.getFirstError());
            }

            // Step 2: Verify task exists
            if (courseRepository.findByCourseTaskId(courseTaskId).isEmpty()) {
                throw new TimerException(TimerErrorCode.TASK_NOT_FOUND, courseTaskId);
            }

            // Step 3: Check for existing active timer
            Optional<TaskTimer> existingTimer =
                    timerRepository.findActiveTimerByUserIdAndTaskId(userId, courseTaskId);

            if (existingTimer.isPresent()) {
                throw new TimerException(TimerErrorCode.TIMER_ALREADY_RUNNING, courseTaskId);
            }

            // Step 4: Create new timer
            TaskTimer timer = TaskTimer.builder()
                    .userId(userId)
                    .courseTaskId(courseTaskId)
                    .startTime(Instant.now())
                    .status(TimerStatus.RUNNING)
                    .durationMillis(0)
                    .build();

            // Step 5: Save timer
            TaskTimer savedTimer = timerRepository.save(timer);
            logger.info("Timer started successfully: {}", savedTimer.getId());

            // Step 6: Adapt to DTO
            return TimerAdapter.toDTO(savedTimer);
        } catch (TimerException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error starting timer", e);
            throw new TimerException(TimerErrorCode.TIMER_START_FAILED, e, e.getMessage());
        }
    }

    @Override
    public TimerDTO stopTimer(UUID timerId, UUID userId) {
        logger.info("Stopping timer: {} for user: {}", timerId, userId);

        try {
            // Step 1: Validate input
            ValidationResult timerValidation = validationService.validateTimerId(timerId);
            if (!timerValidation.isValid()) {
                throw new TimerException(TimerErrorCode.INVALID_TASK_ID, timerValidation.getFirstError());
            }

            ValidationResult userValidation = validationService.validateUserId(userId);
            if (!userValidation.isValid()) {
                throw new TimerException(TimerErrorCode.INVALID_USER_ID, userValidation.getFirstError());
            }

            // Step 2: Find timer
            TaskTimer timer = timerRepository.findById(timerId)
                    .orElseThrow(() -> new TimerException(TimerErrorCode.TIMER_NOT_FOUND, timerId));

            // Step 3: Verify ownership
            if (!timer.getUserId().equals(userId)) {
                throw new TimerException(TimerErrorCode.UNAUTHORIZED_TIMER_ACCESS, userId, timerId);
            }

            // Step 4: Stop timer (entity method handles status validation)
            timer.stop();

            // Step 5: Save updated timer
            TaskTimer stoppedTimer = timerRepository.save(timer);
            logger.info("Timer stopped successfully: {} - Duration: {} seconds",
                    timerId, stoppedTimer.getDurationMillis());

            // Step 6: Adapt to DTO
            return TimerAdapter.toDTO(stoppedTimer);
        } catch (TimerException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error stopping timer: {}", timerId, e);
            throw new TimerException(TimerErrorCode.TIMER_STOP_FAILED, e, e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimerDTO> getTimersByTaskId(UUID courseTaskId) {
        logger.debug("Getting timers for task: {}", courseTaskId);

        ValidationResult taskValidation = validationService.validateCourseTaskId(courseTaskId);
        if (!taskValidation.isValid()) {
            throw new TimerException(TimerErrorCode.INVALID_TASK_ID, taskValidation.getFirstError());
        }

        List<TaskTimer> timers = timerRepository.findTimersByTaskId(courseTaskId);
        return TimerAdapter.toDTOList(timers);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskTimerSummaryDTO getTimerSummaryByTaskId(UUID courseTaskId) {
        logger.debug("Getting timer summary for task: {}", courseTaskId);

        try {
            ValidationResult taskValidation = validationService.validateCourseTaskId(courseTaskId);
            if (!taskValidation.isValid()) {
                throw new TimerException(TimerErrorCode.INVALID_TASK_ID, taskValidation.getFirstError());
            }

            // Get all timer sessions for the task
            List<TaskTimer> allTimers = timerRepository.findTimersByTaskId(courseTaskId);

            // Calculate total time (sum of STOPPED timers)
            long totalTime = timerRepository.calculateTotalTimeByTaskId(courseTaskId);

            // Find active timer if exists
            Optional<TaskTimer> activeTimer = allTimers.stream()
                    .filter(TaskTimer::isRunning)
                    .findFirst();

            return TaskTimerSummaryDTO.builder()
                    .courseTaskId(courseTaskId)
                    .totalTimeMillis(totalTime)
                    .sessionCount(allTimers.size())
                    .sessions(TimerAdapter.toDTOList(allTimers))
                    .activeSession(activeTimer.map(TimerAdapter::toDTO).orElse(null))
                    .build();
        } catch (Exception e) {
            logger.error("Error getting timer summary for task: {}", courseTaskId, e);
            throw new TimerException(TimerErrorCode.TIMER_QUERY_FAILED, e, courseTaskId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalTimeForTask(UUID courseTaskId) {
        logger.debug("Getting total time for task: {}", courseTaskId);

        ValidationResult taskValidation = validationService.validateCourseTaskId(courseTaskId);
        if (!taskValidation.isValid()) {
            throw new TimerException(TimerErrorCode.INVALID_TASK_ID, taskValidation.getFirstError());
        }

        return timerRepository.calculateTotalTimeByTaskId(courseTaskId);
    }

    @Override
    public void stopAllActiveTimersForUser(UUID userId) {
        logger.info("Stopping all active timers for user: {}", userId);

        try {
            ValidationResult userValidation = validationService.validateUserId(userId);
            if (!userValidation.isValid()) {
                throw new TimerException(TimerErrorCode.INVALID_USER_ID, userValidation.getFirstError());
            }

            // Find all active (RUNNING) timers for the user
            List<TaskTimer> activeTimers = timerRepository.findActiveTimersByUserId(userId);

            // Stop each timer and save
            for (TaskTimer timer : activeTimers) {
                timer.stop();
                timerRepository.save(timer);
                logger.info("Stopped timer {} for user {}", timer.getId(), userId);
            }

            logger.info("Stopped {} active timers for user {}", activeTimers.size(), userId);
        } catch (Exception e) {
            logger.error("Error stopping active timers for user: {}", userId, e);
            throw new TimerException(TimerErrorCode.TIMER_STOP_FAILED, e, userId.toString());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public TimerDTO getTimerById(UUID timerId) {
        logger.debug("Getting timer by id: {}", timerId);

        ValidationResult timerValidation = validationService.validateTimerId(timerId);
        if (!timerValidation.isValid()) {
            throw new TimerException(TimerErrorCode.INVALID_TASK_ID, timerValidation.getFirstError());
        }

        TaskTimer timer = timerRepository.findById(timerId)
                .orElseThrow(() -> new TimerException(TimerErrorCode.TIMER_NOT_FOUND, timerId));

        return TimerAdapter.toDTO(timer);
    }
}
