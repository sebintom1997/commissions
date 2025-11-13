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
 * Monthly recognition schedule for commission revenue
 */
@Entity
@Table(name = "recognition_schedule")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecognitionSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commission_plan_id", nullable = false)
    private CommissionPlan commissionPlan;

    @Column(nullable = false)
    private Integer month;  // 1-12 (month number in recognition period)

    @Column(nullable = false)
    private LocalDate recognitionDate;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal plannedAmount;

    @Column(precision = 12, scale = 2)
    private BigDecimal recognizedAmount;

    @Column(length = 20)
    private String status;  // PENDING, RECOGNIZED, PAID

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = "PENDING";
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
