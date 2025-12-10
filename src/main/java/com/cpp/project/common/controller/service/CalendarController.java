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
     * GET /api/calendar/items?year=2024&month=1&userId={userId}&timezone=America/New_York
     */
    @GetMapping("/items")
    public ResponseEntity<ApiSuccessResponse<List<CalendarItemDTO>>> getCalendarItems(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam UUID userId,
            @RequestParam String timezone) {

        // Validate parameters
        if (year == 0 || month == 0 || userId == null || timezone == null || timezone.isEmpty()) {
            throw new CalendarException(CalendarErrorCode.INVALID_DATE,
                    "Year, month, userId, and timezone are required");
        }

        List<CalendarItemDTO> items = calendarService.getItemsForMonth(year, month, userId, timezone);

        ApiSuccessResponse<List<CalendarItemDTO>> response = ApiSuccessResponse.<List<CalendarItemDTO>>builder()
                .data(items)
                .message("Calendar items retrieved successfully")
                .statusCode(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Get all calendar items for a specific month (alternative endpoint with userId in path)
     * GET /api/calendar/user/{userId}/items?year=2024&month=1&timezone=America/New_York
     */
    @GetMapping("/user/{userId}/items")
    public ResponseEntity<ApiSuccessResponse<List<CalendarItemDTO>>> getCalendarItemsByUser(
            @PathVariable UUID userId,
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam String timezone) {

        // Validate parameters
        if (year == 0 || month == 0 || timezone == null || timezone.isEmpty()) {
            throw new CalendarException(CalendarErrorCode.INVALID_DATE,
                    "Year, month, and timezone are required");
        }

        List<CalendarItemDTO> items = calendarService.getItemsForMonth(year, month, userId, timezone);

        ApiSuccessResponse<List<CalendarItemDTO>> response = ApiSuccessResponse.<List<CalendarItemDTO>>builder()
                .data(items)
                .message("Calendar items retrieved successfully")
                .statusCode(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }
}
