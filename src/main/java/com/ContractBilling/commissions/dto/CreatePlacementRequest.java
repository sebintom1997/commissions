package com.ContractBilling.commissions.dto;

import com.ContractBilling.commissions.entity.FeeType;
import com.ContractBilling.commissions.entity.PayType;
import com.ContractBilling.commissions.entity.PlacementType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for creating a new Placement
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatePlacementRequest {

    // Relationships (IDs)
    @NotNull(message = "Salesperson ID is required")
    private Long salespersonId;

    @NotNull(message = "Client ID is required")
    private Long clientId;

    @NotNull(message = "Contractor ID is required")
    private Long contractorId;

    // Basic fields
    @NotNull(message = "Placement type is required")
    private PlacementType placementType;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    private LocalDate endDate;

    // Contractor-specific fields
    @DecimalMin(value = "0.0", message = "Hours per week must be positive")
    private BigDecimal hoursPerWeek;

    @Min(value = 1, message = "Weeks per year must be at least 1")
    @Max(value = 52, message = "Weeks per year cannot exceed 52")
    private Integer weeksPerYear;

    private PayType payType;

    @DecimalMin(value = "0.0", message = "Annual salary must be positive")
    private BigDecimal annualSalary;

    @DecimalMin(value = "0.0", message = "Hourly pay rate must be positive")
    private BigDecimal hourlyPayRate;

    @DecimalMin(value = "0.0", message = "Bill rate must be positive")
    private BigDecimal billRate;

    // Permanent-specific fields
    @DecimalMin(value = "0.0", message = "Placement fee must be positive")
    private BigDecimal placementFee;

    private FeeType feeType;

    @DecimalMin(value = "0.0", message = "Candidate salary must be positive")
    private BigDecimal candidateSalary;

    @Min(value = 1, message = "Recognition period must be at least 1 month")
    @Max(value = 24, message = "Recognition period cannot exceed 24 months")
    private Integer recognitionPeriodMonths;
}
