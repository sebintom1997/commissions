package com.ContractBilling.commissions.dto;

import com.ContractBilling.commissions.entity.PolicySettings;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert between PolicySettings entity and DTOs
 */
@Component
public class SettingsMapper {

    /**
     * Convert PolicySettings entity to SettingsResponse DTO
     */
    public SettingsResponse toResponse(PolicySettings entity) {
        if (entity == null) {
            return null;
        }
        return new SettingsResponse(
                entity.getId(),
                entity.getAdminPercentage(),
                entity.getInsurancePercentage(),
                entity.getLeavePercentage(),
                entity.getPrsiPercentage(),
                entity.getPensionPercentage(),
                entity.getPensionCap(),
                entity.getWeeksPerYear(),
                entity.getFirstContractCommission(),
                entity.getSecondContractCommission(),
                entity.getThirdContractCommission(),
                entity.getDrawdownMinMonth(),
                entity.getDrawdownMaxPerQuarter(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getCreatedAt()
        );
    }

    /**
     * Update existing PolicySettings entity with data from UpdateSettingsRequest DTO
     */
    public void updateEntity(PolicySettings entity, UpdateSettingsRequest request, String updatedBy) {
        if (entity == null || request == null) {
            return;
        }

        entity.setAdminPercentage(request.getAdminPercentage());
        entity.setInsurancePercentage(request.getInsurancePercentage());
        entity.setLeavePercentage(request.getLeavePercentage());
        entity.setPrsiPercentage(request.getPrsiPercentage());
        entity.setPensionPercentage(request.getPensionPercentage());
        entity.setPensionCap(request.getPensionCap());
        entity.setWeeksPerYear(request.getWeeksPerYear());
        entity.setFirstContractCommission(request.getFirstContractCommission());
        entity.setSecondContractCommission(request.getSecondContractCommission());
        entity.setThirdContractCommission(request.getThirdContractCommission());
        entity.setDrawdownMinMonth(request.getDrawdownMinMonth());
        entity.setDrawdownMaxPerQuarter(request.getDrawdownMaxPerQuarter());
        entity.setUpdatedBy(updatedBy);
    }
}
