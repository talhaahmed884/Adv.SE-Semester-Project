package com.cpp.project.todolist.repository;

import com.cpp.project.todolist.entity.ToDoList;
import com.cpp.project.todolist.entity.ToDoListErrorCode;
import com.cpp.project.todolist.entity.ToDoListException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository implementation for ToDoList entity using JPA EntityManager
 */
@Repository
@Transactional
public class ToDoListRepositoryImpl implements ToDoListRepository {
    private static final Logger logger = LoggerFactory.getLogger(ToDoListRepositoryImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public ToDoList save(ToDoList todoList) {
        try {
            if (todoList.getId() == null || !entityManager.contains(todoList)) {
                entityManager.persist(todoList);
                logger.info("Todo list created successfully with name: {}", todoList.getName());
                return todoList;
            } else {
                ToDoList updated = entityManager.merge(todoList);
                logger.info("Todo list updated successfully with name: {}", todoList.getName());
                return updated;
            }
        } catch (PersistenceException e) {
            logger.error("Failed to save todo list with name: {}", todoList.getName(), e);
            throw new ToDoListException(ToDoListErrorCode.TODO_LIST_CREATION_FAILED, e, e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ToDoList> findById(UUID id) {
        try {
            ToDoList todoList = entityManager.find(ToDoList.class, id);
            return Optional.ofNullable(todoList);
        } catch (Exception e) {
            logger.error("Error finding todo list by id: {}", id, e);
            throw new ToDoListException(ToDoListErrorCode.TODO_LIST_NOT_FOUND, e, "id", id);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ToDoList> findByUserId(UUID userId) {
        try {
            TypedQuery<ToDoList> query = entityManager.createQuery(
                    "SELECT t FROM ToDoList t WHERE t.userId = :userId ORDER BY t.createdAt DESC", ToDoList.class);
            query.setParameter("userId", userId);
            return query.getResultList();
        } catch (Exception e) {
            logger.error("Error finding todo lists by userId: {}", userId, e);
            throw new ToDoListException(ToDoListErrorCode.TODO_LIST_NOT_FOUND, e, "userId", userId);
        }
    }

    @Override
    public void deleteById(UUID id) {
        try {
            findById(id).ifPresent(this::delete);
        } catch (Exception e) {
            logger.error("Failed to delete todo list by id: {}", id, e);
            throw new ToDoListException(ToDoListErrorCode.TODO_LIST_DELETION_FAILED, e, id.toString());
        }
    }

    @Override
    public void delete(ToDoList todoList) {
        try {
            if (entityManager.contains(todoList)) {
                entityManager.remove(todoList);
            } else {
                entityManager.remove(entityManager.merge(todoList));
            }
            logger.info("Todo list deleted successfully with id: {}", todoList.getId());
        } catch (Exception e) {
            logger.error("Failed to delete todo list with id: {}", todoList.getId(), e);
            throw new ToDoListException(ToDoListErrorCode.TODO_LIST_DELETION_FAILED, e, e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ToDoList> findAll() {
        try {
            TypedQuery<ToDoList> query = entityManager.createQuery(
                    "SELECT t FROM ToDoList t ORDER BY t.createdAt DESC", ToDoList.class);
            return query.getResultList();
        } catch (Exception e) {
            logger.error("Error retrieving all todo lists", e);
            throw new ToDoListException(ToDoListErrorCode.TODO_LIST_NOT_FOUND, e, "all", "todo lists");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                    "SELECT COUNT(t) FROM ToDoList t", Long.class);
            return query.getSingleResult();
        } catch (Exception e) {
            logger.error("Error counting todo lists", e);
            return 0;
        }
    }
}
