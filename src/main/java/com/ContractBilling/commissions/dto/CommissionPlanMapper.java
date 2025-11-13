package com.ContractBilling.commissions.dto;

import com.ContractBilling.commissions.entity.CommissionPlan;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommissionPlanMapper {

    private final SalespersonMapper salespersonMapper;

    public CommissionPlanResponse toResponse(CommissionPlan entity) {
        if (entity == null) return null;

        return CommissionPlanResponse.builder()
                .id(entity.getId())
                .placementId(entity.getPlacement().getId())
                .salesperson(salespersonMapper.toResponse(entity.getSalesperson()))
                .plannedAmount(entity.getPlannedAmount())
                .confirmedAmount(entity.getConfirmedAmount())
                .recognizedAmount(entity.getRecognizedAmount())
                .paidAmount(entity.getPaidAmount())
                .status(entity.getStatus())
                .recognitionStartDate(entity.getRecognitionStartDate())
                .recognitionEndDate(entity.getRecognitionEndDate())
                .monthsToRecognize(entity.getMonthsToRecognize())
                .monthsRecognized(entity.getMonthsRecognized())
                .eligibleForDrawdown(entity.getEligibleForDrawdown())
                .drawdownMonth(entity.getDrawdownMonth())
                .notes(entity.getNotes())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public CommissionPlan toEntity(CreateCommissionPlanRequest request) {
        if (request == null) return null;

        return CommissionPlan.builder()
                .plannedAmount(request.getPlannedAmount())
                .confirmedAmount(request.getConfirmedAmount())
                .status(request.getStatus())
                .recognitionStartDate(request.getRecognitionStartDate())
                .recognitionEndDate(request.getRecognitionEndDate())
                .monthsToRecognize(request.getMonthsToRecognize())
                .eligibleForDrawdown(request.getEligibleForDrawdown())
                .notes(request.getNotes())
                .build();
    }

    public void updateEntity(CommissionPlan entity, UpdateCommissionPlanRequest request) {
        if (entity == null || request == null) return;

        if (request.getConfirmedAmount() != null) {
            entity.setConfirmedAmount(request.getConfirmedAmount());
        }
        if (request.getRecognizedAmount() != null) {
            entity.setRecognizedAmount(request.getRecognizedAmount());
        }
        if (request.getPaidAmount() != null) {
            entity.setPaidAmount(request.getPaidAmount());
        }
        if (request.getStatus() != null) {
            entity.setStatus(request.getStatus());
        }
        if (request.getRecognitionStartDate() != null) {
            entity.setRecognitionStartDate(request.getRecognitionStartDate());
        }
        if (request.getRecognitionEndDate() != null) {
            entity.setRecognitionEndDate(request.getRecognitionEndDate());
        }
        if (request.getMonthsRecognized() != null) {
            entity.setMonthsRecognized(request.getMonthsRecognized());
        }
        if (request.getEligibleForDrawdown() != null) {
            entity.setEligibleForDrawdown(request.getEligibleForDrawdown());
        }
        if (request.getDrawdownMonth() != null) {
            entity.setDrawdownMonth(request.getDrawdownMonth());
        }
        if (request.getNotes() != null) {
            entity.setNotes(request.getNotes());
        }
    }
}
