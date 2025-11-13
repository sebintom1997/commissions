package com.ContractBilling.commissions.controller;

import com.ContractBilling.commissions.dto.ApiResponse;
import com.ContractBilling.commissions.dto.LedgerResponse;
import com.ContractBilling.commissions.entity.LedgerEntryType;
import com.ContractBilling.commissions.service.LedgerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("/api/ledger")
@RequiredArgsConstructor
@Tag(name = "Ledger", description = "Commission transaction ledger")
public class LedgerController {

    private final LedgerService service;

    @GetMapping("/{id}")
    @Operation(summary = "Get ledger entry by ID")
    public ResponseEntity<ApiResponse<LedgerResponse>> getById(@PathVariable Long id) {
        LedgerResponse entry = service.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<LedgerResponse>builder()
                        .success(true)
                        .message("Ledger entry retrieved")
                        .data(entry)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @GetMapping
    @Operation(summary = "List all ledger entries")
    public ResponseEntity<Page<LedgerResponse>> getAll(
            @PageableDefault(size = 50, sort = "createdAt", direction = DESC) Pageable pageable) {
        return ResponseEntity.ok(service.getAll(pageable));
    }

    @GetMapping("/salesperson/{salespersonId}")
    @Operation(summary = "Get ledger entries by salesperson")
    public ResponseEntity<List<LedgerResponse>> getBySalesperson(@PathVariable Long salespersonId) {
        return ResponseEntity.ok(service.findBySalesperson(salespersonId));
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get ledger entries by type")
    public ResponseEntity<List<LedgerResponse>> getByType(@PathVariable LedgerEntryType type) {
        return ResponseEntity.ok(service.findByType(type));
    }

    @GetMapping("/salesperson/{salespersonId}/type/{type}")
    @Operation(summary = "Get ledger entries by salesperson and type")
    public ResponseEntity<List<LedgerResponse>> getBySalespersonAndType(
            @PathVariable Long salespersonId,
            @PathVariable LedgerEntryType type) {
        return ResponseEntity.ok(service.findBySalespersonAndType(salespersonId, type));
    }

    @GetMapping("/commission-plan/{commissionPlanId}")
    @Operation(summary = "Get ledger entries by commission plan")
    public ResponseEntity<List<LedgerResponse>> getByCommissionPlan(@PathVariable Long commissionPlanId) {
        return ResponseEntity.ok(service.findByCommissionPlan(commissionPlanId));
    }

    @GetMapping("/placement/{placementId}")
    @Operation(summary = "Get ledger entries by placement")
    public ResponseEntity<List<LedgerResponse>> getByPlacement(@PathVariable Long placementId) {
        return ResponseEntity.ok(service.findByPlacement(placementId));
    }

    @GetMapping("/salesperson/{salespersonId}/summary")
    @Operation(summary = "Get salesperson ledger summary")
    public ResponseEntity<?> getSummary(@PathVariable Long salespersonId) {
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Ledger summary retrieved")
                        .data(java.util.Map.of(
                                "totalAccrued", service.getSalespersonTotalAccrued(salespersonId),
                                "totalRecognized", service.getSalespersonTotalRecognized(salespersonId),
                                "totalPaid", service.getSalespersonTotalPaid(salespersonId)
                        ))
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}
