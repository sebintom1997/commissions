package com.ContractBilling.commissions.controller;

import com.ContractBilling.commissions.dto.ApiResponse;
import com.ContractBilling.commissions.dto.CommissionPlanResponse;
import com.ContractBilling.commissions.dto.CreateCommissionPlanRequest;
import com.ContractBilling.commissions.dto.UpdateCommissionPlanRequest;
import com.ContractBilling.commissions.entity.CommissionPlanStatus;
import com.ContractBilling.commissions.service.CommissionPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("/api/commission-plans")
@RequiredArgsConstructor
@Tag(name = "Commission Plans", description = "API for managing commission plans")
public class CommissionPlanController {

    private final CommissionPlanService service;

    @PostMapping
    @Operation(summary = "Create commission plan")
    public ResponseEntity<ApiResponse<CommissionPlanResponse>> create(
            @Valid @RequestBody CreateCommissionPlanRequest request) {
        CommissionPlanResponse created = service.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<CommissionPlanResponse>builder()
                        .success(true)
                        .message("Commission plan created successfully")
                        .data(created)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @GetMapping
    @Operation(summary = "List all commission plans")
    public ResponseEntity<Page<CommissionPlanResponse>> getAll(
            @PageableDefault(size = 20, sort = "createdAt", direction = DESC) Pageable pageable) {
        return ResponseEntity.ok(service.getAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get commission plan by ID")
    public ResponseEntity<ApiResponse<CommissionPlanResponse>> getById(@PathVariable Long id) {
        CommissionPlanResponse plan = service.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<CommissionPlanResponse>builder()
                        .success(true)
                        .message("Commission plan retrieved successfully")
                        .data(plan)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update commission plan")
    public ResponseEntity<ApiResponse<CommissionPlanResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCommissionPlanRequest request) {
        CommissionPlanResponse updated = service.update(id, request);

        return ResponseEntity.ok(
                ApiResponse.<CommissionPlanResponse>builder()
                        .success(true)
                        .message("Commission plan updated successfully")
                        .data(updated)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete commission plan")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Commission plan deleted successfully")
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @GetMapping("/salesperson/{salespersonId}")
    @Operation(summary = "Get plans by salesperson")
    public ResponseEntity<List<CommissionPlanResponse>> getBySalesperson(@PathVariable Long salespersonId) {
        return ResponseEntity.ok(service.findBySalesperson(salespersonId));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Filter by status")
    public ResponseEntity<List<CommissionPlanResponse>> getByStatus(@PathVariable CommissionPlanStatus status) {
        return ResponseEntity.ok(service.findByStatus(status));
    }

    @GetMapping("/salesperson/{salespersonId}/status/{status}")
    @Operation(summary = "Filter by salesperson and status")
    public ResponseEntity<List<CommissionPlanResponse>> getBySalespersonAndStatus(
            @PathVariable Long salespersonId,
            @PathVariable CommissionPlanStatus status) {
        return ResponseEntity.ok(service.findBySalespersonAndStatus(salespersonId, status));
    }

    @GetMapping("/placement/{placementId}")
    @Operation(summary = "Get plan by placement")
    public ResponseEntity<ApiResponse<CommissionPlanResponse>> getByPlacement(@PathVariable Long placementId) {
        CommissionPlanResponse plan = service.findByPlacement(placementId);

        return ResponseEntity.ok(
                ApiResponse.<CommissionPlanResponse>builder()
                        .success(true)
                        .message("Commission plan retrieved")
                        .data(plan)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @GetMapping("/salesperson/{salespersonId}/eligible-drawdown")
    @Operation(summary = "Get plans eligible for drawdown")
    public ResponseEntity<List<CommissionPlanResponse>> getEligibleForDrawdown(@PathVariable Long salespersonId) {
        return ResponseEntity.ok(service.getEligibleForDrawdown(salespersonId));
    }
}
