package com.ContractBilling.commissions.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Ledger entries track all commission transactions
 */
@Entity
@Table(name = "ledger")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ledger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commission_plan_id")
    private CommissionPlan commissionPlan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salesperson_id", nullable = false)
    private Salesperson salesperson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "placement_id")
    private Placement placement;

    // Entry type
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LedgerEntryType entryType;

    // Financial details
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    // Description
    @Column(length = 500)
    private String description;

    // Reference info
    @Column(name = "reference_type", length = 50)
    private String referenceType;  // PLACEMENT, DRAWDOWN, etc.

    @Column(name = "reference_id")
    private Long referenceId;

    // Status
    @Column(length = 20)
    private String status;  // PENDING, COMPLETED, FAILED

    // Notes
    @Column(length = 500)
    private String notes;

    // Audit
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(length = 100)
    private String createdBy;

    @Column(length = 100)
    private String updatedBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = "COMPLETED";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
