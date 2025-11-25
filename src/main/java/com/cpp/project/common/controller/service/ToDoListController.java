package com.cpp.project.common.controller.service;

import com.cpp.project.common.controller.dto.ApiSuccessResponse;
import com.cpp.project.todolist.dto.*;
import com.cpp.project.todolist.entity.ToDoListErrorCode;
import com.cpp.project.todolist.entity.ToDoListException;
import com.cpp.project.todolist.service.ToDoListService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * REST Controller for ToDoList operations
 * Follows REST API best practices with ApiSuccessResponse wrapper
 */
@RestController
@RequestMapping("/api/todolists")
public class ToDoListController {
    private final ToDoListService todoListService;

    public ToDoListController(ToDoListService todoListService) {
        this.todoListService = todoListService;
    }

    /**
     * Create a new todo list
     * POST /api/todolists
     */
    @PostMapping
    public ResponseEntity<ApiSuccessResponse<ToDoListDTO>> createToDoList(
            @RequestBody CreateToDoListRequestDTO request) {

        // Validate request
        if (request.isEmpty()) {
            throw new ToDoListException(ToDoListErrorCode.INVALID_TODO_LIST_DATA,
                    "Todo list name and userId are required");
        }

        ToDoListDTO todoList = todoListService.createToDoList(
                request.getName(),
                request.getUserId()
        );

        ApiSuccessResponse<ToDoListDTO> response = ApiSuccessResponse.<ToDoListDTO>builder()
                .data(todoList)
                .message("Todo list created successfully")
                .statusCode(HttpStatus.CREATED.value())
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get a todo list by ID
     * GET /api/todolists/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<ToDoListDTO>> getToDoListById(
            @PathVariable UUID id) {

        ToDoListDTO todoList = todoListService.getToDoListById(id);

        ApiSuccessResponse<ToDoListDTO> response = ApiSuccessResponse.<ToDoListDTO>builder()
                .data(todoList)
                .message("Todo list retrieved successfully")
                .statusCode(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Get all todo lists for a user
     * GET /api/todolists/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiSuccessResponse<List<ToDoListDTO>>> getToDoListsByUserId(
            @PathVariable UUID userId) {

        List<ToDoListDTO> todoLists = todoListService.getToDoListsByUserId(userId);

        ApiSuccessResponse<List<ToDoListDTO>> response = ApiSuccessResponse.<List<ToDoListDTO>>builder()
                .data(todoLists)
                .message("Todo lists retrieved successfully")
                .statusCode(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Update a todo list
     * PUT /api/todolists/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<ToDoListDTO>> updateToDoList(
            @PathVariable UUID id,
            @RequestBody UpdateToDoListRequestDTO request) {

        // Validate at least one field is provided
        if (request.isEmpty()) {
            throw new ToDoListException(ToDoListErrorCode.INVALID_TODO_LIST_DATA,
                    "Todo list name must be provided");
        }

        ToDoListDTO todoList = todoListService.updateToDoList(id, request.getName());

        ApiSuccessResponse<ToDoListDTO> response = ApiSuccessResponse.<ToDoListDTO>builder()
                .data(todoList)
                .message("Todo list updated successfully")
                .statusCode(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Delete a todo list
     * DELETE /api/todolists/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<String>> deleteToDoList(
            @PathVariable UUID id) {

        todoListService.deleteToDoList(id);

        ApiSuccessResponse<String> response = ApiSuccessResponse.<String>builder()
                .data("Todo list deleted successfully")
                .message("Deletion successful")
                .statusCode(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Add a task to a todo list
     * POST /api/todolists/{todoListId}/tasks
     */
    @PostMapping("/{todoListId}/tasks")
    public ResponseEntity<ApiSuccessResponse<ToDoListTaskDTO>> addTaskToList(
            @PathVariable UUID todoListId,
            @RequestBody AddToDoListTaskRequestDTO request) {

        // Validate request
        if (request.isEmpty()) {
            throw new ToDoListException(ToDoListErrorCode.INVALID_TASK_DATA,
                    "Task description and deadline are required");
        }

        ToDoListTaskDTO task = todoListService.addTaskToList(
                todoListId,
                request.getDescription(),
                request.getDeadline()
        );

        ApiSuccessResponse<ToDoListTaskDTO> response = ApiSuccessResponse.<ToDoListTaskDTO>builder()
                .data(task)
                .message("Task added successfully")
                .statusCode(HttpStatus.CREATED.value())
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Mark a task as complete
     * PUT /api/todolists/{todoListId}/tasks/{taskId}/complete
     */
    @PutMapping("/{todoListId}/tasks/{taskId}/complete")
    public ResponseEntity<ApiSuccessResponse<ToDoListTaskDTO>> markTaskComplete(
            @PathVariable UUID todoListId,
            @PathVariable UUID taskId) {

        ToDoListTaskDTO task = todoListService.markTaskComplete(todoListId, taskId);

        ApiSuccessResponse<ToDoListTaskDTO> response = ApiSuccessResponse.<ToDoListTaskDTO>builder()
                .data(task)
                .message("Task marked as complete")
                .statusCode(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Get aggregated deadlines from all tasks
     * GET /api/todolists/{todoListId}/deadlines
     */
    @GetMapping("/{todoListId}/deadlines")
    public ResponseEntity<ApiSuccessResponse<List<Date>>> getAggregatedDeadlines(
            @PathVariable UUID todoListId) {

        List<Date> deadlines = todoListService.getAggregatedDeadlines(todoListId);

        ApiSuccessResponse<List<Date>> response = ApiSuccessResponse.<List<Date>>builder()
                .data(deadlines)
                .message("Deadlines retrieved successfully")
                .statusCode(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }
}
