package com.ContractBilling.commissions.controller;

import com.ContractBilling.commissions.dto.ApiResponse;
import com.ContractBilling.commissions.service.ReportingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Commission reporting and dashboards")
public class ReportingController {

    private final ReportingService reportingService;

    @GetMapping("/salesperson/{salespersonId}/dashboard")
    @Operation(summary = "Get salesperson dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboard(@PathVariable Long salespersonId) {
        Map<String, Object> dashboard = reportingService.getSalespersonDashboard(salespersonId);

        return ResponseEntity.ok(
                ApiResponse.<Map<String, Object>>builder()
                        .success(true)
                        .message("Dashboard retrieved")
                        .data(dashboard)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @GetMapping("/salesperson/{salespersonId}/period")
    @Operation(summary = "Get period summary")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPeriodSummary(
            @PathVariable Long salespersonId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        Map<String, Object> summary = reportingService.getPeriodSummary(salespersonId, startDate, endDate);

        return ResponseEntity.ok(
                ApiResponse.<Map<String, Object>>builder()
                        .success(true)
                        .message("Period summary retrieved")
                        .data(summary)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @GetMapping("/salesperson/{salespersonId}/commissions")
    @Operation(summary = "Get commission breakdown")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCommissionBreakdown(@PathVariable Long salespersonId) {
        Map<String, Object> breakdown = reportingService.getCommissionByPlacement(salespersonId);

        return ResponseEntity.ok(
                ApiResponse.<Map<String, Object>>builder()
                        .success(true)
                        .message("Commission breakdown retrieved")
                        .data(breakdown)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @GetMapping("/salesperson/{salespersonId}/recognition")
    @Operation(summary = "Get recognition status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRecognitionStatus(@PathVariable Long salespersonId) {
        Map<String, Object> status = reportingService.getRecognitionStatus(salespersonId);

        return ResponseEntity.ok(
                ApiResponse.<Map<String, Object>>builder()
                        .success(true)
                        .message("Recognition status retrieved")
                        .data(status)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @GetMapping("/salesperson/{salespersonId}/drawdowns")
    @Operation(summary = "Get drawdown history")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDrawdownHistory(@PathVariable Long salespersonId) {
        Map<String, Object> history = reportingService.getDrawdownHistory(salespersonId);

        return ResponseEntity.ok(
                ApiResponse.<Map<String, Object>>builder()
                        .success(true)
                        .message("Drawdown history retrieved")
                        .data(history)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @GetMapping("/top-performers")
    @Operation(summary = "Get top performers")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTopPerformers(
            @RequestParam(defaultValue = "10") int limit) {

        Map<String, Object> report = reportingService.getTopPerformers(limit);

        return ResponseEntity.ok(
                ApiResponse.<Map<String, Object>>builder()
                        .success(true)
                        .message("Top performers retrieved")
                        .data(report)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @GetMapping("/health")
    @Operation(summary = "Get system health")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSystemHealth() {
        Map<String, Object> health = reportingService.getSystemHealth();

        return ResponseEntity.ok(
                ApiResponse.<Map<String, Object>>builder()
                        .success(true)
                        .message("System health retrieved")
                        .data(health)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}
