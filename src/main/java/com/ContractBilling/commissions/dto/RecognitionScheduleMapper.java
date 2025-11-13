package com.ContractBilling.commissions.dto;

import com.ContractBilling.commissions.entity.RecognitionSchedule;
import org.springframework.stereotype.Component;

@Component
public class RecognitionScheduleMapper {

    public RecognitionScheduleResponse toResponse(RecognitionSchedule entity) {
        if (entity == null) return null;

        return RecognitionScheduleResponse.builder()
                .id(entity.getId())
                .commissionPlanId(entity.getCommissionPlan().getId())
                .month(entity.getMonth())
                .recognitionDate(entity.getRecognitionDate())
                .plannedAmount(entity.getPlannedAmount())
                .recognizedAmount(entity.getRecognizedAmount())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
