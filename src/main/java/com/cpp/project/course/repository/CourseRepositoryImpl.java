package com.cpp.project.course.repository;

import com.cpp.project.course.entity.Course;
import com.cpp.project.course.entity.CourseErrorCode;
import com.cpp.project.course.entity.CourseException;
import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository implementation for Course entity using JPA EntityManager
 */
@Repository
@Transactional
public class CourseRepositoryImpl implements CourseRepository {
    private static final Logger logger = LoggerFactory.getLogger(CourseRepositoryImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Course save(Course course) {
        try {
            if (course.getId() == null || !entityManager.contains(course)) {
                entityManager.persist(course);
                logger.info("Course created successfully with code: {}", course.getCode());
                return course;
            } else {
                Course updated = entityManager.merge(course);
                logger.info("Course updated successfully with code: {}", course.getCode());
                return updated;
            }
        } catch (PersistenceException e) {
            logger.error("Failed to save course with code: {}", course.getCode(), e);
            throw new CourseException(CourseErrorCode.COURSE_CREATION_FAILED, e, e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Course> findById(UUID id) {
        try {
            Course course = entityManager.find(Course.class, id);
            return Optional.ofNullable(course);
        } catch (Exception e) {
            logger.error("Error finding course by id: {}", id, e);
            throw new CourseException(CourseErrorCode.COURSE_NOT_FOUND, e, "id", id);
        }
    }

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
    public List<Course> findByUserId(UUID userId) {
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
    public void deleteById(UUID id) {
        try {
            findById(id).ifPresent(this::delete);
        } catch (Exception e) {
            logger.error("Failed to delete course by id: {}", id, e);
            throw new CourseException(CourseErrorCode.COURSE_DELETION_FAILED, e, id.toString());
        }
    }

    @Override
    public void delete(Course course) {
        try {
            if (entityManager.contains(course)) {
                entityManager.remove(course);
            } else {
                entityManager.remove(entityManager.merge(course));
            }
            logger.info("Course deleted successfully with id: {}", course.getId());
        } catch (Exception e) {
            logger.error("Failed to delete course with id: {}", course.getId(), e);
            throw new CourseException(CourseErrorCode.COURSE_DELETION_FAILED, e, e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Course> findAll() {
        try {
            TypedQuery<Course> query = entityManager.createQuery(
                    "SELECT c FROM Course c ORDER BY c.createdAt DESC", Course.class);
            return query.getResultList();
        } catch (Exception e) {
            logger.error("Error retrieving all courses", e);
            throw new CourseException(CourseErrorCode.COURSE_NOT_FOUND, e, "all", "courses");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                    "SELECT COUNT(c) FROM Course c", Long.class);
            return query.getSingleResult();
        } catch (Exception e) {
            logger.error("Error counting courses", e);
            return 0;
        }
    }
}
