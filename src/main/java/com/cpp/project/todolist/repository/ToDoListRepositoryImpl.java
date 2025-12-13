package com.cpp.project.todolist.repository;

import com.cpp.project.common.repository.BaseJpaRepository;
import com.cpp.project.todolist.entity.ToDoList;
import com.cpp.project.todolist.entity.ToDoListErrorCode;
import com.cpp.project.todolist.entity.ToDoListException;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Repository implementation for ToDoList entity using JPA EntityManager
 * Extends BaseJpaRepository for common CRUD operations
 */
@Repository
@Transactional
public class ToDoListRepositoryImpl extends BaseJpaRepository<ToDoList, UUID> implements ToDoListRepository {
    private static final Logger logger = LoggerFactory.getLogger(ToDoListRepositoryImpl.class);

    // ========== BaseJpaRepository Implementation ==========

    @Override
    protected Class<ToDoList> getEntityClass() {
        return ToDoList.class;
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected String getEntityName() {
        return "Todo list";
    }

    @Override
    protected String getEntityIdentifier(ToDoList todoList) {
        return todoList.getName();
    }

    @Override
    protected boolean isNew(ToDoList todoList) {
        return todoList.getId() == null;
    }

    @Override
    protected RuntimeException createNotFoundException(UUID id) {
        return new ToDoListException(ToDoListErrorCode.TODO_LIST_NOT_FOUND, "id", id);
    }

    @Override
    protected RuntimeException createNotFoundException(String fieldName, Object value) {
        return new ToDoListException(ToDoListErrorCode.TODO_LIST_NOT_FOUND, fieldName, value);
    }

    @Override
    protected RuntimeException createSaveException(Exception cause) {
        return new ToDoListException(ToDoListErrorCode.TODO_LIST_CREATION_FAILED, cause, cause.getMessage());
    }

    @Override
    protected RuntimeException createDeleteException(Object id) {
        return new ToDoListException(ToDoListErrorCode.TODO_LIST_DELETION_FAILED, id.toString());
    }

    // ========== Domain-Specific Methods ==========

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
}
