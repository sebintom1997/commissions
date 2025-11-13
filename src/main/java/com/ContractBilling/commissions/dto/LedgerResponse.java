package com.ContractBilling.commissions.dto;

import com.ContractBilling.commissions.entity.LedgerEntryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LedgerResponse {

    private Long id;

    private Long commissionPlanId;

    private SalespersonResponse salesperson;

    private Long placementId;

    private LedgerEntryType entryType;

    private BigDecimal amount;

    private String description;

    private String referenceType;

    private Long referenceId;

    private String status;

    private String notes;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdBy;

    private String updatedBy;
}
