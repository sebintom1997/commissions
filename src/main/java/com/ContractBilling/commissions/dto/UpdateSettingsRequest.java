package com.ContractBilling.commissions.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for updating PolicySettings
 * All fields are required - this is a complete update
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateSettingsRequest {

    // Overhead Percentages
    @NotNull(message = "Admin percentage is required")
    @DecimalMin(value = "0.0", message = "Admin percentage must be at least 0")
    @DecimalMax(value = "100.0", message = "Admin percentage must not exceed 100")
    private BigDecimal adminPercentage;

    @NotNull(message = "Insurance percentage is required")
    @DecimalMin(value = "0.0", message = "Insurance percentage must be at least 0")
    @DecimalMax(value = "100.0", message = "Insurance percentage must not exceed 100")
    private BigDecimal insurancePercentage;

    @NotNull(message = "Leave percentage is required")
    @DecimalMin(value = "0.0", message = "Leave percentage must be at least 0")
    @DecimalMax(value = "100.0", message = "Leave percentage must not exceed 100")
    private BigDecimal leavePercentage;

    @NotNull(message = "PRSI percentage is required")
    @DecimalMin(value = "0.0", message = "PRSI percentage must be at least 0")
    @DecimalMax(value = "100.0", message = "PRSI percentage must not exceed 100")
    private BigDecimal prsiPercentage;

    @NotNull(message = "Pension percentage is required")
    @DecimalMin(value = "0.0", message = "Pension percentage must be at least 0")
    @DecimalMax(value = "100.0", message = "Pension percentage must not exceed 100")
    private BigDecimal pensionPercentage;

    @DecimalMin(value = "0.0", message = "Pension cap must be at least 0")
    private BigDecimal pensionCap;

    // Contract Settings
    @NotNull(message = "Weeks per year is required")
    @Min(value = 1, message = "Weeks per year must be at least 1")
    @Max(value = 52, message = "Weeks per year must not exceed 52")
    private Integer weeksPerYear;

    // Commission Tier Percentages
    @NotNull(message = "First contract commission is required")
    @DecimalMin(value = "0.0", message = "First contract commission must be at least 0")
    @DecimalMax(value = "100.0", message = "First contract commission must not exceed 100")
    private BigDecimal firstContractCommission;

    @NotNull(message = "Second contract commission is required")
    @DecimalMin(value = "0.0", message = "Second contract commission must be at least 0")
    @DecimalMax(value = "100.0", message = "Second contract commission must not exceed 100")
    private BigDecimal secondContractCommission;

    @NotNull(message = "Third contract commission is required")
    @DecimalMin(value = "0.0", message = "Third contract commission must be at least 0")
    @DecimalMax(value = "100.0", message = "Third contract commission must not exceed 100")
    private BigDecimal thirdContractCommission;

    // Drawdown Rules
    @NotNull(message = "Drawdown minimum month is required")
    @Min(value = 1, message = "Drawdown minimum month must be at least 1")
    @Max(value = 12, message = "Drawdown minimum month must not exceed 12")
    private Integer drawdownMinMonth;

    @NotNull(message = "Drawdown max per quarter is required")
    @Min(value = 1, message = "Drawdown max per quarter must be at least 1")
    private Integer drawdownMaxPerQuarter;
}
