package com.cpp.project.dashboard.controller;

import com.cpp.project.common.controller.dto.ApiSuccessResponse;
import com.cpp.project.dashboard.dto.DashboardSummaryDTO;
import com.cpp.project.dashboard.service.DashboardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller for Dashboard operations
 * Provides endpoints for dashboard data aggregation
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * Get dashboard summary for a user
     * Returns course study time distribution and upcoming tasks
     *
     * @param userId User ID
     * @return Dashboard summary with course study times and upcoming tasks
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiSuccessResponse<DashboardSummaryDTO>> getDashboardSummary(
            @PathVariable UUID userId) {

        DashboardSummaryDTO summary = dashboardService.getDashboardSummary(userId);

        ApiSuccessResponse<DashboardSummaryDTO> response = ApiSuccessResponse.<DashboardSummaryDTO>builder()
                .data(summary)
                .message("Dashboard summary retrieved successfully")
                .statusCode(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }
}
