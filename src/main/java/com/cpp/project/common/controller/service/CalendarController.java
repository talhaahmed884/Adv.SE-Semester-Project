package com.cpp.project.common.controller.service;

import com.cpp.project.calendar.dto.CalendarItemDTO;
import com.cpp.project.calendar.entity.CalendarErrorCode;
import com.cpp.project.calendar.entity.CalendarException;
import com.cpp.project.calendar.service.CalendarService;
import com.cpp.project.common.controller.dto.ApiSuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for Calendar operations
 * Provides endpoints for viewing aggregated tasks from Course and ToDoList in calendar format
 */
@RestController
@RequestMapping("/api/calendar")
public class CalendarController {
    private final CalendarService calendarService;

    public CalendarController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    /**
     * Get all calendar items for a specific month
     * GET /api/calendar/items?year=2024&month=1&userId={userId}
     */
    @GetMapping("/items")
    public ResponseEntity<ApiSuccessResponse<List<CalendarItemDTO>>> getCalendarItems(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam UUID userId) {

        // Validate parameters
        if (year == 0 || month == 0 || userId == null) {
            throw new CalendarException(CalendarErrorCode.INVALID_DATE,
                    "Year, month, and userId are required");
        }

        List<CalendarItemDTO> items = calendarService.getItemsForMonth(year, month, userId);

        ApiSuccessResponse<List<CalendarItemDTO>> response = ApiSuccessResponse.<List<CalendarItemDTO>>builder()
                .data(items)
                .message("Calendar items retrieved successfully")
                .statusCode(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Get all calendar items for a specific month (alternative endpoint with userId in path)
     * GET /api/calendar/user/{userId}/items?year=2024&month=1
     */
    @GetMapping("/user/{userId}/items")
    public ResponseEntity<ApiSuccessResponse<List<CalendarItemDTO>>> getCalendarItemsByUser(
            @PathVariable UUID userId,
            @RequestParam int year,
            @RequestParam int month) {

        // Validate parameters
        if (year == 0 || month == 0) {
            throw new CalendarException(CalendarErrorCode.INVALID_DATE,
                    "Year and month are required");
        }

        List<CalendarItemDTO> items = calendarService.getItemsForMonth(year, month, userId);

        ApiSuccessResponse<List<CalendarItemDTO>> response = ApiSuccessResponse.<List<CalendarItemDTO>>builder()
                .data(items)
                .message("Calendar items retrieved successfully")
                .statusCode(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }
}
