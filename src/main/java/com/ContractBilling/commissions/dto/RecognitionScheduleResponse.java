package com.ContractBilling.commissions.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecognitionScheduleResponse {

    private Long id;

    private Long commissionPlanId;

    private Integer month;

    private LocalDate recognitionDate;

    private BigDecimal plannedAmount;

    private BigDecimal recognizedAmount;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
