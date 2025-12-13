package com.cpp.project.course.repository;

import com.cpp.project.common.repository.BaseJpaRepository;
import com.cpp.project.course.entity.Course;
import com.cpp.project.course.entity.CourseErrorCode;
import com.cpp.project.course.entity.CourseException;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository implementation for Course entity using JPA EntityManager
 * Extends BaseJpaRepository for common CRUD operations
 */
@Repository
@Transactional
public class CourseRepositoryImpl extends BaseJpaRepository<Course, UUID> implements CourseRepository {
    private static final Logger logger = LoggerFactory.getLogger(CourseRepositoryImpl.class);

    // ========== BaseJpaRepository Implementation ==========

    @Override
    protected Class<Course> getEntityClass() {
        return Course.class;
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected String getEntityName() {
        return "Course";
    }

    @Override
    protected String getEntityIdentifier(Course course) {
        return course.getCode();
    }

    @Override
    protected boolean isNew(Course course) {
        return course.getId() == null;
    }

    @Override
    protected RuntimeException createNotFoundException(UUID id) {
        return new CourseException(CourseErrorCode.COURSE_NOT_FOUND, "id", id);
    }

    @Override
    protected RuntimeException createNotFoundException(String fieldName, Object value) {
        return new CourseException(CourseErrorCode.COURSE_NOT_FOUND, fieldName, value);
    }

    @Override
    protected RuntimeException createSaveException(Exception cause) {
        return new CourseException(CourseErrorCode.COURSE_CREATION_FAILED, cause, cause.getMessage());
    }

    @Override
    protected RuntimeException createDeleteException(Object id) {
        return new CourseException(CourseErrorCode.COURSE_DELETION_FAILED, id.toString());
    }

    // ========== Domain-Specific Methods ==========

    @Override
    @Transactional(readOnly = true)
    public Optional<Course> findByCode(String code) {
        try {
            TypedQuery<Course> query = entityManager.createQuery(
                    "SELECT c FROM Course c WHERE c.code = :code", Course.class);
            query.setParameter("code", code);
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            logger.debug("Course not found with code: {}", code);
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Error finding course by code: {}", code, e);
            return Optional.empty();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<Course> findByUserId(UUID userId) {
        try {
            TypedQuery<Course> query = entityManager.createQuery(
                    "SELECT c FROM Course c WHERE c.userId = :userId ORDER BY c.createdAt DESC", Course.class);
            query.setParameter("userId", userId);
            return query.getResultList();
        } catch (Exception e) {
            logger.error("Error finding courses by userId: {}", userId, e);
            throw new CourseException(CourseErrorCode.COURSE_NOT_FOUND, e, "userId", userId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCode(String code) {
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                    "SELECT COUNT(c) FROM Course c WHERE c.code = :code", Long.class);
            query.setParameter("code", code);
            return query.getSingleResult() > 0;
        } catch (Exception e) {
            logger.error("Error checking course existence by code: {}", code, e);
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCodeAndUserId(String code, UUID userId) {
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                    "SELECT COUNT(c) FROM Course c WHERE c.code = :code AND c.userId = :userId", Long.class);
            query.setParameter("code", code);
            query.setParameter("userId", userId);
            return query.getSingleResult() > 0;
        } catch (Exception e) {
            logger.error("Error checking course existence by code and userId: {} for user: {}", code, userId, e);
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Course> findByCourseTaskId(UUID taskId) {
        try {
            TypedQuery<Course> query = entityManager.createQuery(
                    "SELECT c FROM Course c JOIN c.tasks t WHERE t.id = :taskId", Course.class);
            query.setParameter("taskId", taskId);
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            logger.debug("Course not found with task id: {}", taskId);
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Error finding course by task id: {}", taskId, e);
            return Optional.empty();
        }
    }
}
