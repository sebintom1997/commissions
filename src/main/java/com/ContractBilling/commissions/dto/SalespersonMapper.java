package com.ContractBilling.commissions.dto;

import com.ContractBilling.commissions.entity.Salesperson;
import org.springframework.stereotype.Component;

@Component
public class SalespersonMapper {

    // Convert Salesperson entity to SalespersonResponse DTO
    public SalespersonResponse toResponse(Salesperson entity) {
        if (entity == null) {
            return null;
        }
        return new SalespersonResponse(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    // convert SalespersonRequest DTO to Salesperson entity
    public Salesperson toEntity(CreateSalespersonRequest request) {
        if (request == null) {
            return null;
        }
        Salesperson entity = new Salesperson();
        entity.setName(request.getName());
        entity.setEmail(request.getEmail());
        return entity;
    }

    // Update existing Salesperson entity with data from SalespersonRequest DTO
    public void updateEntity(Salesperson entity, UpdateSalespersonRequest request){
        if(entity == null || request == null){
            return;
        }
        if(request.getName() != null){
            entity.setName(request.getName());
        }
        if (request.getEmail() != null){
            entity.setEmail(request.getEmail());
        }
        if (request.getStatus() != null){
            entity.setStatus(request.getStatus());
        }
    }
}
