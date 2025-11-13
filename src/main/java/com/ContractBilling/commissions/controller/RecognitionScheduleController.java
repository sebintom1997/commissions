package com.ContractBilling.commissions.controller;

import com.ContractBilling.commissions.dto.ApiResponse;
import com.ContractBilling.commissions.dto.RecognitionScheduleResponse;
import com.ContractBilling.commissions.service.RevenueRecognitionEngine;
import com.ContractBilling.commissions.repository.RecognitionScheduleRepository;
import com.ContractBilling.commissions.dto.RecognitionScheduleMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recognition")
@RequiredArgsConstructor
@Tag(name = "Revenue Recognition", description = "Commission revenue recognition schedules")
public class RecognitionScheduleController {

    private final RevenueRecognitionEngine recognitionEngine;
    private final RecognitionScheduleRepository repository;
    private final RecognitionScheduleMapper mapper;

    @GetMapping("/plan/{commissionPlanId}")
    @Operation(summary = "Get recognition schedule for plan")
    public ResponseEntity<List<RecognitionScheduleResponse>> getByPlan(@PathVariable Long commissionPlanId) {
        List<RecognitionScheduleResponse> schedules = repository
                .findByCommissionPlanId(commissionPlanId).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(schedules);
    }

    @PostMapping("/process")
    @Operation(summary = "Process all due recognitions")
    public ResponseEntity<ApiResponse<Object>> processAllDue(@RequestParam(required = false) LocalDate asOfDate) {
        LocalDate processDate = asOfDate != null ? asOfDate : LocalDate.now();
        int count = recognitionEngine.recognizeAllDue(processDate);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Processed " + count + " recognitions")
                        .data(java.util.Map.of("processed", count))
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @GetMapping("/{scheduleId}")
    @Operation(summary = "Get recognition schedule entry")
    public ResponseEntity<RecognitionScheduleResponse> getById(@PathVariable Long scheduleId) {
        var schedule = repository.findById(scheduleId).orElseThrow();
        return ResponseEntity.ok(mapper.toResponse(schedule));
    }
}
