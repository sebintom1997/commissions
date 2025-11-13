package com.ContractBilling.commissions.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing System-wide Policy Settings
 * This is a SINGLETON - only ONE record should exist in the database
 */
@Entity
@Table(name = "policy_settings")
@Data
public class PolicySettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Overhead Percentages
    @Column(name = "admin_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal adminPercentage;

    @Column(name = "insurance_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal insurancePercentage;

    @Column(name = "leave_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal leavePercentage;

    @Column(name = "prsi_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal prsiPercentage;

    @Column(name = "pension_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal pensionPercentage;

    @Column(name = "pension_cap", precision = 12, scale = 2)
    private BigDecimal pensionCap;

    // Contract Settings
    @Column(name = "weeks_per_year", nullable = false)
    private Integer weeksPerYear;

    // Commission Tier Percentages
    @Column(name = "first_contract_commission", nullable = false, precision = 5, scale = 2)
    private BigDecimal firstContractCommission;

    @Column(name = "second_contract_commission", nullable = false, precision = 5, scale = 2)
    private BigDecimal secondContractCommission;

    @Column(name = "third_contract_commission", nullable = false, precision = 5, scale = 2)
    private BigDecimal thirdContractCommission;

    // Drawdown Rules
    @Column(name = "drawdown_min_month", nullable = false)
    private Integer drawdownMinMonth;

    @Column(name = "drawdown_max_per_quarter", nullable = false)
    private Integer drawdownMaxPerQuarter;

    // Audit fields
    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Set timestamp on creation
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Update timestamp on modification
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
