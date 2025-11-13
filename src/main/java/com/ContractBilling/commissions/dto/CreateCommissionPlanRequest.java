package com.ContractBilling.commissions.dto;

import com.ContractBilling.commissions.entity.CommissionPlanStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCommissionPlanRequest {

    @NotNull
    private Long placementId;

    @NotNull
    private Long salespersonId;

    @NotNull
    private BigDecimal plannedAmount;

    private BigDecimal confirmedAmount;

    private CommissionPlanStatus status;

    private LocalDate recognitionStartDate;

    private LocalDate recognitionEndDate;

    private Integer monthsToRecognize;

    private Boolean eligibleForDrawdown;

    private String notes;
}
