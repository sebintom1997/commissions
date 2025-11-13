package com.ContractBilling.commissions.dto;

import com.ContractBilling.commissions.entity.CommissionPlanStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCommissionPlanRequest {

    private BigDecimal confirmedAmount;

    private BigDecimal recognizedAmount;

    private BigDecimal paidAmount;

    private CommissionPlanStatus status;

    private LocalDate recognitionStartDate;

    private LocalDate recognitionEndDate;

    private Integer monthsRecognized;

    private Boolean eligibleForDrawdown;

    private Integer drawdownMonth;

    private String notes;
}
