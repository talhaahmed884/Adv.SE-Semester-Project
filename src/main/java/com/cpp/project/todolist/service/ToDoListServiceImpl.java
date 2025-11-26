package com.cpp.project.todolist.service;

import com.cpp.project.common.sanitization.adapter.ToDoListSanitizer;
import com.cpp.project.common.sanitization.adapter.ToDoListTaskSanitizer;
import com.cpp.project.common.validation.entity.ValidationResult;
import com.cpp.project.common.validation.service.ToDoListValidationService;
import com.cpp.project.todolist.adapter.ToDoListAdapter;
import com.cpp.project.todolist.adapter.ToDoListTaskAdapter;
import com.cpp.project.todolist.dto.ToDoListDTO;
import com.cpp.project.todolist.dto.ToDoListTaskDTO;
import com.cpp.project.todolist.entity.ToDoList;
import com.cpp.project.todolist.entity.ToDoListErrorCode;
import com.cpp.project.todolist.entity.ToDoListException;
import com.cpp.project.todolist.entity.ToDoListTask;
import com.cpp.project.todolist.repository.ToDoListRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service implementation for ToDoList operations
 * Includes validation, sanitization, and exception handling
 */
@Service
@Transactional
public class ToDoListServiceImpl implements ToDoListService {
    private static final Logger logger = LoggerFactory.getLogger(ToDoListServiceImpl.class);

    private final ToDoListRepository todoListRepository;
    private final ToDoListValidationService validationService;
    private final ToDoListSanitizer listSanitizer;
    private final ToDoListTaskSanitizer taskSanitizer;

    public ToDoListServiceImpl(ToDoListRepository todoListRepository,
                               ToDoListValidationService validationService,
                               ToDoListSanitizer listSanitizer,
                               ToDoListTaskSanitizer taskSanitizer) {
        this.todoListRepository = todoListRepository;
        this.validationService = validationService;
        this.listSanitizer = listSanitizer;
        this.taskSanitizer = taskSanitizer;
    }

    @Override
    public ToDoListDTO createToDoList(String name, UUID userId) {
        logger.info("Creating todo list: {}", name);

        try {
            // Step 1: Sanitize input
            String sanitizedName = listSanitizer.sanitizeName(name);

            // Step 2: Validate sanitized input
            ValidationResult nameValidation = validationService.validateName(sanitizedName);
            if (!nameValidation.isValid()) {
                throw new ToDoListException(ToDoListErrorCode.INVALID_TODO_LIST_NAME, nameValidation.getFirstError());
            }

            // Step 3: Create todo list
            ToDoList todoList = ToDoList.builder()
                    .name(sanitizedName)
                    .userId(userId)
                    .build();

            ToDoList savedList = todoListRepository.save(todoList);
            logger.info("Todo list created successfully: {}", savedList.getName());

            return ToDoListAdapter.toDTO(savedList);
        } catch (ToDoListException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error creating todo list", e);
            throw new ToDoListException(ToDoListErrorCode.TODO_LIST_CREATION_FAILED, e, e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ToDoListDTO getToDoListById(UUID id) {
        logger.debug("Getting todo list by id: {}", id);

        ToDoList todoList = todoListRepository.findById(id)
                .orElseThrow(() -> new ToDoListException(ToDoListErrorCode.TODO_LIST_NOT_FOUND, "id", id));

        return ToDoListAdapter.toDTO(todoList);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ToDoListDTO> getToDoListsByUserId(UUID userId) {
        logger.debug("Getting todo lists for user: {}", userId);

        List<ToDoList> todoLists = todoListRepository.findByUserId(userId);

        return todoLists.stream()
                .map(ToDoListAdapter::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ToDoListDTO updateToDoList(UUID id, String name) {
        logger.info("Updating todo list: {}", id);

        try {
            // Step 1: Sanitize input
            String sanitizedName = listSanitizer.sanitizeName(name);

            // Step 2: Validate sanitized input
            ValidationResult nameValidation = validationService.validateName(sanitizedName);
            if (!nameValidation.isValid()) {
                throw new ToDoListException(ToDoListErrorCode.INVALID_TODO_LIST_NAME, nameValidation.getFirstError());
            }

            // Step 3: Update todo list
            ToDoList todoList = todoListRepository.findById(id)
                    .orElseThrow(() -> new ToDoListException(ToDoListErrorCode.TODO_LIST_NOT_FOUND, "id", id));

            todoList.setName(sanitizedName);
            ToDoList updatedList = todoListRepository.save(todoList);

            logger.info("Todo list updated successfully: {}", id);
            return ToDoListAdapter.toDTO(updatedList);
        } catch (ToDoListException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error updating todo list: {}", id, e);
            throw new ToDoListException(ToDoListErrorCode.TODO_LIST_UPDATE_FAILED, e, e.getMessage());
        }
    }

    @Override
    public void deleteToDoList(UUID id) {
        logger.info("Deleting todo list: {}", id);

        try {
            if (todoListRepository.findById(id).isEmpty()) {
                throw new ToDoListException(ToDoListErrorCode.TODO_LIST_NOT_FOUND, "id", id);
            }

            todoListRepository.deleteById(id);
            logger.info("Todo list deleted successfully: {}", id);
        } catch (ToDoListException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error deleting todo list: {}", id, e);
            throw new ToDoListException(ToDoListErrorCode.TODO_LIST_DELETION_FAILED, e, id.toString());
        }
    }

    @Override
    public ToDoListTaskDTO addTaskToList(UUID todoListId, String description, Date deadline) {
        logger.info("Adding task to todo list: {}", todoListId);

        try {
            // Step 1: Sanitize input
            String sanitizedDescription = taskSanitizer.sanitizeDescription(description);

            // Step 2: Validate sanitized input
            ValidationResult descriptionValidation = validationService.validateTaskDescription(sanitizedDescription);
            if (!descriptionValidation.isValid()) {
                throw new ToDoListException(ToDoListErrorCode.INVALID_TASK_DESCRIPTION, descriptionValidation.getFirstError());
            }

            ValidationResult deadlineValidation = validationService.validateTaskDeadline(deadline);
            if (!deadlineValidation.isValid()) {
                throw new ToDoListException(ToDoListErrorCode.INVALID_TASK_DEADLINE, deadlineValidation.getFirstError());
            }

            // Step 3: Find list and add task
            ToDoList todoList = todoListRepository.findById(todoListId)
                    .orElseThrow(() -> new ToDoListException(ToDoListErrorCode.TODO_LIST_NOT_FOUND, "id", todoListId));

            ToDoListTask task = todoList.addTask(sanitizedDescription, deadline);

            todoListRepository.save(todoList);
            logger.info("Task added to todo list successfully");

            return ToDoListTaskAdapter.toDTO(task);
        } catch (ToDoListException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error adding task to todo list: {}", todoListId, e);
            throw new ToDoListException(ToDoListErrorCode.TASK_CREATION_FAILED, e, e.getMessage());
        }
    }

    @Override
    public ToDoListTaskDTO markTaskComplete(UUID todoListId, UUID taskId) {
        logger.info("Marking task complete: {} in todo list: {}", taskId, todoListId);

        try {
            ToDoList todoList = todoListRepository.findById(todoListId)
                    .orElseThrow(() -> new ToDoListException(ToDoListErrorCode.TODO_LIST_NOT_FOUND, "id", todoListId));

            ToDoListTask task = todoList.getTasks().stream()
                    .filter(t -> t.getId().equals(taskId))
                    .findFirst()
                    .orElseThrow(() -> new ToDoListException(ToDoListErrorCode.TASK_NOT_FOUND, taskId));

            task.markComplete();

            todoListRepository.save(todoList);
            logger.info("Task marked complete successfully");

            return ToDoListTaskAdapter.toDTO(task);
        } catch (ToDoListException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error marking task complete: {}", taskId, e);
            throw new ToDoListException(ToDoListErrorCode.TASK_UPDATE_FAILED, e, e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Date> getAggregatedDeadlines(UUID todoListId) {
        logger.debug("Getting aggregated deadlines for todo list: {}", todoListId);

        ToDoList todoList = todoListRepository.findById(todoListId)
                .orElseThrow(() -> new ToDoListException(ToDoListErrorCode.TODO_LIST_NOT_FOUND, "id", todoListId));

        return todoList.getAggregatedDeadlines();
    }
}
