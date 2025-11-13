package com.ContractBilling.commissions.dto;

import com.ContractBilling.commissions.entity.Contractor;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert between Contractor entities and DTOs
 */
@Component
public class ContractorMapper {

    /**
     * Convert Contractor entity to ContractorResponse DTO
     */
    public ContractorResponse toResponse(Contractor entity) {
        if (entity == null) {
            return null;
        }
        return new ContractorResponse(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getType(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    /**
     * Convert CreateContractorRequest DTO to Contractor entity
     */
    public Contractor toEntity(CreateContractorRequest request) {
        if (request == null) {
            return null;
        }
        Contractor entity = new Contractor();
        entity.setName(request.getName());
        entity.setEmail(request.getEmail());
        entity.setPhone(request.getPhone());
        entity.setType(request.getType());
        return entity;
    }

    /**
     * Update existing Contractor entity with data from UpdateContractorRequest DTO
     */
    public void updateEntity(Contractor entity, UpdateContractorRequest request) {
        if (entity == null || request == null) {
            return;
        }
        if (request.getName() != null) {
            entity.setName(request.getName());
        }
        if (request.getEmail() != null) {
            entity.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            entity.setPhone(request.getPhone());
        }
        if (request.getType() != null) {
            entity.setType(request.getType());
        }
        if (request.getStatus() != null) {
            entity.setStatus(request.getStatus());
        }
    }
}
