package com.cpp.project.todolist.repository;

import com.cpp.project.todolist.entity.ToDoList;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository implementation for ToDoList entity using JPA EntityManager
 */
@Repository
public class ToDoListRepositoryImpl implements ToDoListRepository {
    private static final Logger logger = LoggerFactory.getLogger(ToDoListRepositoryImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public ToDoList save(ToDoList todoList) {
        if (todoList.getId() == null) {
            entityManager.persist(todoList);
            logger.debug("Persisted new todo list: {}", todoList.getName());
            return todoList;
        } else {
            ToDoList merged = entityManager.merge(todoList);
            logger.debug("Updated todo list: {}", todoList.getName());
            return merged;
        }
    }

    @Override
    public Optional<ToDoList> findById(UUID id) {
        try {
            ToDoList todoList = entityManager.find(ToDoList.class, id);
            return Optional.ofNullable(todoList);
        } catch (Exception e) {
            logger.error("Error finding todo list by id: {}", id, e);
            return Optional.empty();
        }
    }

    @Override
    public List<ToDoList> findByUserId(UUID userId) {
        try {
            TypedQuery<ToDoList> query = entityManager.createQuery(
                    "SELECT t FROM ToDoList t WHERE t.userId = :userId ORDER BY t.createdAt DESC", ToDoList.class);
            query.setParameter("userId", userId);
            return query.getResultList();
        } catch (Exception e) {
            logger.error("Error finding todo lists by userId: {}", userId, e);
            return List.of();
        }
    }

    @Override
    public void deleteById(UUID id) {
        try {
            ToDoList todoList = entityManager.find(ToDoList.class, id);
            if (todoList != null) {
                entityManager.remove(todoList);
                logger.debug("Deleted todo list: {}", id);
            }
        } catch (Exception e) {
            logger.error("Error deleting todo list by id: {}", id, e);
        }
    }

    @Override
    public List<ToDoList> findAll() {
        try {
            TypedQuery<ToDoList> query = entityManager.createQuery(
                    "SELECT t FROM ToDoList t ORDER BY t.createdAt DESC", ToDoList.class);
            return query.getResultList();
        } catch (Exception e) {
            logger.error("Error finding all todo lists", e);
            return List.of();
        }
    }
}
