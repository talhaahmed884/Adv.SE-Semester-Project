package com.cpp.project.course.repository;

import com.cpp.project.course.entity.Course;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository implementation for Course entity using JPA EntityManager
 */
@Repository
public class CourseRepositoryImpl implements CourseRepository {
    private static final Logger logger = LoggerFactory.getLogger(CourseRepositoryImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Course save(Course course) {
        if (course.getId() == null) {
            entityManager.persist(course);
            logger.debug("Persisted new course: {}", course.getCode());
            return course;
        } else {
            Course merged = entityManager.merge(course);
            logger.debug("Updated course: {}", course.getCode());
            return merged;
        }
    }

    @Override
    public Optional<Course> findById(UUID id) {
        try {
            Course course = entityManager.find(Course.class, id);
            return Optional.ofNullable(course);
        } catch (Exception e) {
            logger.error("Error finding course by id: {}", id, e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Course> findByCode(String code) {
        try {
            TypedQuery<Course> query = entityManager.createQuery(
                    "SELECT c FROM Course c WHERE c.code = :code", Course.class);
            query.setParameter("code", code);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Error finding course by code: {}", code, e);
            return Optional.empty();
        }
    }

    @Override
    public List<Course> findByUserId(UUID userId) {
        try {
            TypedQuery<Course> query = entityManager.createQuery(
                    "SELECT c FROM Course c WHERE c.userId = :userId ORDER BY c.createdAt DESC", Course.class);
            query.setParameter("userId", userId);
            return query.getResultList();
        } catch (Exception e) {
            logger.error("Error finding courses by userId: {}", userId, e);
            return List.of();
        }
    }

    @Override
    public boolean existsByCode(String code) {
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                    "SELECT COUNT(c) FROM Course c WHERE c.code = :code", Long.class);
            query.setParameter("code", code);
            return query.getSingleResult() > 0;
        } catch (Exception e) {
            logger.error("Error checking if course exists by code: {}", code, e);
            return false;
        }
    }

    @Override
    public void deleteById(UUID id) {
        try {
            Course course = entityManager.find(Course.class, id);
            if (course != null) {
                entityManager.remove(course);
                logger.debug("Deleted course: {}", id);
            }
        } catch (Exception e) {
            logger.error("Error deleting course by id: {}", id, e);
        }
    }

    @Override
    public List<Course> findAll() {
        try {
            TypedQuery<Course> query = entityManager.createQuery(
                    "SELECT c FROM Course c ORDER BY c.createdAt DESC", Course.class);
            return query.getResultList();
        } catch (Exception e) {
            logger.error("Error finding all courses", e);
            return List.of();
        }
    }
}
