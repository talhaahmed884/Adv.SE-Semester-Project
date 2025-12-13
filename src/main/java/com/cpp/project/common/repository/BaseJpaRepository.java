package com.cpp.project.common.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Base repository implementation providing common CRUD operations for JPA entities
 * <p>
 * This abstract class eliminates duplication across repository implementations by
 * providing standard operations: save, findById, findAll, delete, deleteById, count
 * <p>
 * Subclasses must implement:
 * - getEntityClass() - return the entity class type
 * - getLogger() - return the logger instance
 * - getEntityName() - return a human-readable entity name for logging
 * - createNotFoundException() - create appropriate not found exception
 * - createSaveException() - create appropriate save exception
 * - createDeleteException() - create appropriate delete exception
 *
 * @param <T>  Entity type
 * @param <ID> ID type
 */
@Transactional
public abstract class BaseJpaRepository<T, ID> {

    @PersistenceContext
    protected EntityManager entityManager;

    /**
     * Save or update an entity
     *
     * @param entity Entity to save
     * @return Saved entity
     */
    public T save(T entity) {
        try {
            if (isNew(entity) || !entityManager.contains(entity)) {
                entityManager.persist(entity);
                getLogger().info("{} created successfully: {}", getEntityName(), getEntityIdentifier(entity));
                return entity;
            } else {
                T updated = entityManager.merge(entity);
                getLogger().info("{} updated successfully: {}", getEntityName(), getEntityIdentifier(entity));
                return updated;
            }
        } catch (PersistenceException e) {
            getLogger().error("Failed to save {}: {}", getEntityName(), getEntityIdentifier(entity), e);
            throw createSaveException(e);
        }
    }

    /**
     * Find entity by ID
     *
     * @param id Entity ID
     * @return Optional containing entity if found
     */
    @Transactional(readOnly = true)
    public Optional<T> findById(ID id) {
        try {
            T entity = entityManager.find(getEntityClass(), id);
            return Optional.ofNullable(entity);
        } catch (Exception e) {
            getLogger().error("Error finding {} by id: {}", getEntityName(), id, e);
            throw createNotFoundException(id);
        }
    }

    /**
     * Find all entities, ordered by creation date descending
     *
     * @return List of all entities
     */
    @Transactional(readOnly = true)
    public List<T> findAll() {
        try {
            TypedQuery<T> query = entityManager.createQuery(
                    "SELECT e FROM " + getEntityClass().getSimpleName() + " e ORDER BY e.createdAt DESC",
                    getEntityClass());
            return query.getResultList();
        } catch (Exception e) {
            getLogger().error("Error retrieving all {}", getEntityName() + "s", e);
            throw createNotFoundException("all", getEntityName() + "s");
        }
    }

    /**
     * Delete entity by ID
     *
     * @param id Entity ID
     */
    public void deleteById(ID id) {
        try {
            findById(id).ifPresent(this::delete);
        } catch (Exception e) {
            getLogger().error("Failed to delete {} by id: {}", getEntityName(), id, e);
            throw createDeleteException(id);
        }
    }

    /**
     * Delete entity
     *
     * @param entity Entity to delete
     */
    public void delete(T entity) {
        try {
            if (entityManager.contains(entity)) {
                entityManager.remove(entity);
            } else {
                entityManager.remove(entityManager.merge(entity));
            }
            getLogger().info("{} deleted successfully: {}", getEntityName(), getEntityIdentifier(entity));
        } catch (Exception e) {
            getLogger().error("Failed to delete {}: {}", getEntityName(), getEntityIdentifier(entity), e);
            throw createDeleteException(getEntityIdentifier(entity));
        }
    }

    /**
     * Count total number of entities
     *
     * @return Entity count
     */
    @Transactional(readOnly = true)
    public long count() {
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                    "SELECT COUNT(e) FROM " + getEntityClass().getSimpleName() + " e",
                    Long.class);
            return query.getSingleResult();
        } catch (Exception e) {
            getLogger().error("Error counting {}", getEntityName() + "s", e);
            return 0;
        }
    }

    // ========== Abstract Methods (Subclasses Must Implement) ==========

    /**
     * Get the entity class type
     *
     * @return Entity class
     */
    protected abstract Class<T> getEntityClass();

    /**
     * Get the logger instance
     *
     * @return Logger
     */
    protected abstract Logger getLogger();

    /**
     * Get human-readable entity name for logging (e.g., "Course", "ToDoList")
     *
     * @return Entity name
     */
    protected abstract String getEntityName();

    /**
     * Get entity identifier for logging (e.g., course code, list name, entity ID)
     *
     * @param entity Entity
     * @return Identifier string
     */
    protected abstract String getEntityIdentifier(T entity);

    /**
     * Check if entity is new (not yet persisted)
     *
     * @param entity Entity to check
     * @return true if entity is new
     */
    protected abstract boolean isNew(T entity);

    /**
     * Create not found exception for this entity type
     *
     * @param id Entity ID
     * @return RuntimeException
     */
    protected abstract RuntimeException createNotFoundException(ID id);

    /**
     * Create not found exception with custom field name
     *
     * @param fieldName Field name
     * @param value     Field value
     * @return RuntimeException
     */
    protected abstract RuntimeException createNotFoundException(String fieldName, Object value);

    /**
     * Create save exception for this entity type
     *
     * @param cause Original exception
     * @return RuntimeException
     */
    protected abstract RuntimeException createSaveException(Exception cause);

    /**
     * Create delete exception for this entity type
     *
     * @param id Entity ID or identifier
     * @return RuntimeException
     */
    protected abstract RuntimeException createDeleteException(Object id);
}
