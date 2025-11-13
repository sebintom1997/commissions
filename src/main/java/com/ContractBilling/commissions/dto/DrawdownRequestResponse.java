package com.ContractBilling.commissions.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DrawdownRequestResponse {

    private Long id;

    private SalespersonResponse salesperson;

    private BigDecimal requestedAmount;

    private BigDecimal approvedAmount;

    private String status;

    private LocalDate requestDate;

    private LocalDate approvedDate;

    private LocalDate paidDate;

    private Integer quarterYear;

    private Integer quarterNumber;

    private String paymentMethod;

    private String referenceNumber;

    private String notes;

    private String rejectionReason;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String approvedBy;

    private String paidBy;
}
