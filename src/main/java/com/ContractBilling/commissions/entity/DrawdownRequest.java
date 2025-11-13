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
 * Drawdown/Payout request for commission payments
 */
@Entity
@Table(name = "drawdown_request")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DrawdownRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salesperson_id", nullable = false)
    private Salesperson salesperson;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal requestedAmount;

    @Column(precision = 12, scale = 2)
    private BigDecimal approvedAmount;

    @Column(length = 20)
    private String status;  // PENDING, APPROVED, PAID, REJECTED

    @Column(nullable = false)
    private LocalDate requestDate;

    private LocalDate approvedDate;

    private LocalDate paidDate;

    // Quarter info
    @Column(name = "quarter_year")
    private Integer quarterYear;

    @Column(name = "quarter_number")
    private Integer quarterNumber;

    // Payment method
    @Column(length = 50)
    private String paymentMethod;  // BANK_TRANSFER, CHECK, etc.

    // Reference number
    @Column(length = 100)
    private String referenceNumber;

    // Notes
    @Column(length = 500)
    private String notes;

    @Column(length = 500)
    private String rejectionReason;

    // Audit
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(length = 100)
    private String approvedBy;

    @Column(length = 100)
    private String paidBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = "PENDING";
        }
        if (requestDate == null) {
            requestDate = LocalDate.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
