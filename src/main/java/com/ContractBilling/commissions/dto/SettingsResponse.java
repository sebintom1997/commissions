package com.ContractBilling.commissions.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for PolicySettings response
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SettingsResponse {

    private Long id;

    // Overhead Percentages
    private BigDecimal adminPercentage;
    private BigDecimal insurancePercentage;
    private BigDecimal leavePercentage;
    private BigDecimal prsiPercentage;
    private BigDecimal pensionPercentage;
    private BigDecimal pensionCap;

    // Contract Settings
    private Integer weeksPerYear;

    // Commission Tier Percentages
    private BigDecimal firstContractCommission;
    private BigDecimal secondContractCommission;
    private BigDecimal thirdContractCommission;

    // Drawdown Rules
    private Integer drawdownMinMonth;
    private Integer drawdownMaxPerQuarter;

    // Audit fields
    private String updatedBy;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;
}
