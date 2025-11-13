package com.ContractBilling.commissions.controller;

import com.ContractBilling.commissions.dto.ApiResponse;
import com.ContractBilling.commissions.dto.DrawdownRequestResponse;
import com.ContractBilling.commissions.entity.DrawdownRequest;
import com.ContractBilling.commissions.repository.DrawdownRequestRepository;
import com.ContractBilling.commissions.repository.SalespersonRepository;
import com.ContractBilling.commissions.service.DrawdownEngine;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/drawdowns")
@RequiredArgsConstructor
@Tag(name = "Drawdowns", description = "Commission drawdown/payout requests")
public class DrawdownRequestController {

    private final DrawdownRequestRepository repository;
    private final DrawdownEngine engine;
    private final SalespersonRepository salespersonRepository;

    @PostMapping
    @Operation(summary = "Request drawdown")
    public ResponseEntity<ApiResponse<DrawdownRequestResponse>> requestDrawdown(
            @RequestParam Long salespersonId,
            @RequestParam BigDecimal amount) {

        var salesperson = salespersonRepository.findById(salespersonId).orElseThrow();

        DrawdownRequest request = DrawdownRequest.builder()
                .salesperson(salesperson)
                .requestedAmount(amount)
                .status("PENDING")
                .requestDate(LocalDate.now())
                .build();

        repository.save(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<DrawdownRequestResponse>builder()
                        .success(true)
                        .message("Drawdown request created")
                        .data(toResponse(request))
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @GetMapping("/salesperson/{salespersonId}")
    @Operation(summary = "Get drawdown requests for salesperson")
    public ResponseEntity<List<DrawdownRequestResponse>> getBySalesperson(@PathVariable Long salespersonId) {
        var salesperson = salespersonRepository.findById(salespersonId).orElseThrow();
        return ResponseEntity.ok(
                repository.findBySalesperson(salesperson).stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList())
        );
    }

    @PostMapping("/{id}/approve")
    @Operation(summary = "Approve drawdown")
    public ResponseEntity<ApiResponse<Object>> approve(
            @PathVariable Long id,
            @RequestParam String approvedBy) {

        var request = repository.findById(id).orElseThrow();
        engine.approveDrawdown(request, approvedBy);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Drawdown approved")
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @PostMapping("/{id}/reject")
    @Operation(summary = "Reject drawdown")
    public ResponseEntity<ApiResponse<Object>> reject(
            @PathVariable Long id,
            @RequestParam String reason,
            @RequestParam String rejectedBy) {

        var request = repository.findById(id).orElseThrow();
        engine.rejectDrawdown(request, reason, rejectedBy);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Drawdown rejected")
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @PostMapping("/{id}/pay")
    @Operation(summary = "Process payment")
    public ResponseEntity<ApiResponse<Object>> pay(
            @PathVariable Long id,
            @RequestParam String paidBy) {

        var request = repository.findById(id).orElseThrow();
        engine.processPayment(request, paidBy);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Payment processed")
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @GetMapping("/salesperson/{salespersonId}/available")
    @Operation(summary = "Get available balance")
    public ResponseEntity<ApiResponse<Object>> getAvailable(@PathVariable Long salespersonId) {
        var salesperson = salespersonRepository.findById(salespersonId).orElseThrow();
        BigDecimal available = engine.getAvailableBalance(salesperson);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Available balance")
                        .data(java.util.Map.of("availableBalance", available))
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    private DrawdownRequestResponse toResponse(DrawdownRequest request) {
        return DrawdownRequestResponse.builder()
                .id(request.getId())
                .requestedAmount(request.getRequestedAmount())
                .approvedAmount(request.getApprovedAmount())
                .status(request.getStatus())
                .requestDate(request.getRequestDate())
                .approvedDate(request.getApprovedDate())
                .paidDate(request.getPaidDate())
                .quarterYear(request.getQuarterYear())
                .quarterNumber(request.getQuarterNumber())
                .paymentMethod(request.getPaymentMethod())
                .referenceNumber(request.getReferenceNumber())
                .notes(request.getNotes())
                .rejectionReason(request.getRejectionReason())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .approvedBy(request.getApprovedBy())
                .paidBy(request.getPaidBy())
                .build();
    }
}
