package com.ContractBilling.commissions.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Tracks commission plan for each placement
 * Manages commission lifecycle: planned → confirmed → recognized → paid
 */
@Entity
@Table(name = "commission_plan")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommissionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "placement_id", nullable = false)
    private Placement placement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salesperson_id", nullable = false)
    private Salesperson salesperson;

    // Planned commission (from placement)
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal plannedAmount;

    // Confirmed amount (may differ from planned)
    @Column(precision = 12, scale = 2)
    private BigDecimal confirmedAmount;

    // Recognized amount (portion recognized as revenue)
    @Column(precision = 12, scale = 2)
    private BigDecimal recognizedAmount;

    // Amount paid out
    @Column(precision = 12, scale = 2)
    private BigDecimal paidAmount;

    // Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommissionPlanStatus status;

    // Recognition period
    @Column(name = "recognition_start_date")
    private LocalDate recognitionStartDate;

    @Column(name = "recognition_end_date")
    private LocalDate recognitionEndDate;

    @Column(name = "months_to_recognize")
    private Integer monthsToRecognize;

    // Months already recognized (for partial recognition)
    @Column(name = "months_recognized")
    private Integer monthsRecognized;

    // Drawdown info
    @Column(name = "eligible_for_drawdown")
    private Boolean eligibleForDrawdown;

    @Column(name = "drawdown_month")
    private Integer drawdownMonth;

    // Notes
    @Column(length = 500)
    private String notes;

    // Audit
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = CommissionPlanStatus.PLANNED;
        }
        if (monthsRecognized == null) {
            monthsRecognized = 0;
        }
        if (paidAmount == null) {
            paidAmount = BigDecimal.ZERO;
        }
        if (recognizedAmount == null) {
            recognizedAmount = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
