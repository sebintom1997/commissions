package com.ContractBilling.commissions.dto;

import com.ContractBilling.commissions.entity.Ledger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LedgerMapper {

    private final SalespersonMapper salespersonMapper;

    public LedgerResponse toResponse(Ledger entity) {
        if (entity == null) return null;

        return LedgerResponse.builder()
                .id(entity.getId())
                .commissionPlanId(entity.getCommissionPlan() != null ? entity.getCommissionPlan().getId() : null)
                .salesperson(salespersonMapper.toResponse(entity.getSalesperson()))
                .placementId(entity.getPlacement() != null ? entity.getPlacement().getId() : null)
                .entryType(entity.getEntryType())
                .amount(entity.getAmount())
                .description(entity.getDescription())
                .referenceType(entity.getReferenceType())
                .referenceId(entity.getReferenceId())
                .status(entity.getStatus())
                .notes(entity.getNotes())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }
}
