package com.ContractBilling.commissions.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing a Placement
 * A placement connects a Contractor to a Client through a Salesperson
 */
@Entity
@Table(name = "placement")
@Data
public class Placement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salesperson_id", nullable = false)
    private Salesperson salesperson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contractor_id", nullable = false)
    private Contractor contractor;

    // Basic fields
    @Column(name = "placement_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PlacementType placementType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PlacementStatus status;

    // Dates
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    // Contractor-specific fields
    @Column(name = "hours_per_week", precision = 5, scale = 2)
    private BigDecimal hoursPerWeek;

    @Column(name = "weeks_per_year")
    private Integer weeksPerYear;

    // Pay basis
    @Column(name = "pay_type")
    @Enumerated(EnumType.STRING)
    private PayType payType;

    @Column(name = "annual_salary", precision = 12, scale = 2)
    private BigDecimal annualSalary;

    @Column(name = "hourly_pay_rate", precision = 8, scale = 2)
    private BigDecimal hourlyPayRate;

    // Bill basis
    @Column(name = "bill_rate", precision = 8, scale = 2)
    private BigDecimal billRate;

    @Column(name = "margin_percentage", precision = 5, scale = 2)
    private BigDecimal marginPercentage;

    // Overheads
    @Column(name = "admin_percentage", precision = 5, scale = 2)
    private BigDecimal adminPercentage;

    @Column(name = "insurance_percentage", precision = 5, scale = 2)
    private BigDecimal insurancePercentage;

    @Column(name = "fixed_costs", precision = 10, scale = 2)
    private BigDecimal fixedCosts;

    // Calculated fields (stored for audit/reporting)
    @Column(name = "hourly_pay_cost", precision = 8, scale = 2)
    private BigDecimal hourlyPayCost;

    @Column(name = "margin_per_hour", precision = 8, scale = 2)
    private BigDecimal marginPerHour;

    @Column(name = "weekly_margin", precision = 10, scale = 2)
    private BigDecimal weeklyMargin;

    @Column(name = "gross_annual_margin", precision = 12, scale = 2)
    private BigDecimal grossAnnualMargin;

    @Column(name = "net_annual_margin", precision = 12, scale = 2)
    private BigDecimal netAnnualMargin;

    // Commission fields
    @Column(name = "sequence_number")
    private Integer sequenceNumber;

    @Column(name = "commission_percentage", precision = 5, scale = 2)
    private BigDecimal commissionPercentage;

    @Column(name = "commission_total", precision = 12, scale = 2)
    private BigDecimal commissionTotal;

    // Permanent placement specific fields
    @Column(name = "placement_fee", precision = 12, scale = 2)
    private BigDecimal placementFee;

    @Column(name = "fee_type")
    @Enumerated(EnumType.STRING)
    private FeeType feeType;

    @Column(name = "candidate_salary", precision = 12, scale = 2)
    private BigDecimal candidateSalary;

    @Column(name = "recognition_period_months")
    private Integer recognitionPeriodMonths;

    // Audit fields
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Set default values before persisting
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = PlacementStatus.DRAFT;
        }
    }

    /**
     * Update timestamp before updating
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
