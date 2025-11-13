package com.ContractBilling.commissions.dto;

import com.ContractBilling.commissions.entity.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for Placement response
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlacementResponse {

    private Long id;

    // Nested summaries of related entities
    private SalespersonResponse salesperson;
    private ClientResponse client;
    private ContractorResponse contractor;

    // Basic fields
    private PlacementType placementType;
    private PlacementStatus status;

    // Dates
    private LocalDate startDate;
    private LocalDate endDate;

    // Contractor-specific fields
    private BigDecimal hoursPerWeek;
    private Integer weeksPerYear;

    // Pay basis
    private PayType payType;
    private BigDecimal annualSalary;
    private BigDecimal hourlyPayRate;

    // Bill basis
    private BigDecimal billRate;
    private BigDecimal marginPercentage;

    // Overheads
    private BigDecimal adminPercentage;
    private BigDecimal insurancePercentage;
    private BigDecimal fixedCosts;

    // Calculated fields
    private BigDecimal hourlyPayCost;
    private BigDecimal marginPerHour;
    private BigDecimal weeklyMargin;
    private BigDecimal grossAnnualMargin;
    private BigDecimal netAnnualMargin;

    // Commission fields
    private Integer sequenceNumber;
    private BigDecimal commissionPercentage;
    private BigDecimal commissionTotal;

    // Permanent-specific fields
    private BigDecimal placementFee;
    private FeeType feeType;
    private BigDecimal candidateSalary;
    private Integer recognitionPeriodMonths;

    // Audit fields
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
