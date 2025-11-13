package com.ContractBilling.commissions.dto;

import com.ContractBilling.commissions.entity.CommissionPlanStatus;
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
public class CommissionPlanResponse {

    private Long id;

    private Long placementId;

    private SalespersonResponse salesperson;

    private BigDecimal plannedAmount;

    private BigDecimal confirmedAmount;

    private BigDecimal recognizedAmount;

    private BigDecimal paidAmount;

    private CommissionPlanStatus status;

    private LocalDate recognitionStartDate;

    private LocalDate recognitionEndDate;

    private Integer monthsToRecognize;

    private Integer monthsRecognized;

    private Boolean eligibleForDrawdown;

    private Integer drawdownMonth;

    private String notes;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
