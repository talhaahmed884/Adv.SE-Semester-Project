package com.cpp.project.timer.repository;

import com.cpp.project.timer.entity.TaskTimer;
import com.cpp.project.timer.entity.TimerErrorCode;
import com.cpp.project.timer.entity.TimerException;
import com.cpp.project.timer.entity.TimerStatus;
import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository implementation for TaskTimer entity using JPA EntityManager
 */
@Repository
@Transactional
public class TimerRepositoryImpl implements TimerRepository {
    private static final Logger logger = LoggerFactory.getLogger(TimerRepositoryImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public TaskTimer save(TaskTimer timer) {
        try {
            if (timer.getId() == null || !entityManager.contains(timer)) {
                entityManager.persist(timer);
                logger.info("Timer created successfully with id: {}", timer.getId());
                return timer;
            } else {
                TaskTimer updated = entityManager.merge(timer);
                logger.info("Timer updated successfully with id: {}", timer.getId());
                return updated;
            }
        } catch (PersistenceException e) {
            logger.error("Failed to save timer: {}", timer.getId(), e);
            throw new TimerException(TimerErrorCode.TIMER_START_FAILED, e, e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TaskTimer> findById(UUID id) {
        try {
            TaskTimer timer = entityManager.find(TaskTimer.class, id);
            return Optional.ofNullable(timer);
        } catch (Exception e) {
            logger.error("Error finding timer by id: {}", id, e);
            throw new TimerException(TimerErrorCode.TIMER_NOT_FOUND, e, id);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TaskTimer> findActiveTimerByUserIdAndTaskId(UUID userId, UUID courseTaskId) {
        try {
            TypedQuery<TaskTimer> query = entityManager.createQuery(
                    "SELECT t FROM TaskTimer t WHERE t.userId = :userId " +
                            "AND t.courseTaskId = :taskId AND t.status = :status",
                    TaskTimer.class);
            query.setParameter("userId", userId);
            query.setParameter("taskId", courseTaskId);
            query.setParameter("status", TimerStatus.RUNNING);

            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            logger.debug("No active timer found for user {} and task {}", userId, courseTaskId);
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Error finding active timer for user {} and task {}", userId, courseTaskId, e);
            return Optional.empty();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskTimer> findTimersByTaskId(UUID courseTaskId) {
        try {
            TypedQuery<TaskTimer> query = entityManager.createQuery(
                    "SELECT t FROM TaskTimer t WHERE t.courseTaskId = :taskId " +
                            "ORDER BY t.startTime DESC",
                    TaskTimer.class);
            query.setParameter("taskId", courseTaskId);
            return query.getResultList();
        } catch (Exception e) {
            logger.error("Error finding timers by task id: {}", courseTaskId, e);
            throw new TimerException(TimerErrorCode.TIMER_QUERY_FAILED, e, courseTaskId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskTimer> findTimersByUserId(UUID userId) {
        try {
            TypedQuery<TaskTimer> query = entityManager.createQuery(
                    "SELECT t FROM TaskTimer t WHERE t.userId = :userId " +
                            "ORDER BY t.startTime DESC",
                    TaskTimer.class);
            query.setParameter("userId", userId);
            return query.getResultList();
        } catch (Exception e) {
            logger.error("Error finding timers by user id: {}", userId, e);
            throw new TimerException(TimerErrorCode.TIMER_QUERY_FAILED, e, userId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long calculateTotalTimeByTaskId(UUID courseTaskId) {
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                    "SELECT COALESCE(SUM(t.durationMillis), 0) FROM TaskTimer t " +
                            "WHERE t.courseTaskId = :taskId AND t.status = :status",
                    Long.class);
            query.setParameter("taskId", courseTaskId);
            query.setParameter("status", TimerStatus.STOPPED);

            return query.getSingleResult();
        } catch (Exception e) {
            logger.error("Error calculating total time for task: {}", courseTaskId, e);
            return 0;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskTimer> findActiveTimersByUserId(UUID userId) {
        try {
            TypedQuery<TaskTimer> query = entityManager.createQuery(
                    "SELECT t FROM TaskTimer t WHERE t.userId = :userId " +
                            "AND t.status = :status",
                    TaskTimer.class);
            query.setParameter("userId", userId);
            query.setParameter("status", TimerStatus.RUNNING);
            return query.getResultList();
        } catch (Exception e) {
            logger.error("Error finding active timers for user: {}", userId, e);
            return List.of();
        }
    }

    @Override
    public void delete(TaskTimer timer) {
        try {
            if (entityManager.contains(timer)) {
                entityManager.remove(timer);
            } else {
                entityManager.remove(entityManager.merge(timer));
            }
            logger.info("Timer deleted successfully with id: {}", timer.getId());
        } catch (Exception e) {
            logger.error("Failed to delete timer with id: {}", timer.getId(), e);
            throw new TimerException(TimerErrorCode.TIMER_QUERY_FAILED, e, timer.getId());
        }
    }

    @Override
    public void deleteById(UUID id) {
        try {
            findById(id).ifPresent(this::delete);
        } catch (Exception e) {
            logger.error("Failed to delete timer by id: {}", id, e);
            throw new TimerException(TimerErrorCode.TIMER_QUERY_FAILED, e, id);
        }
    }
}
