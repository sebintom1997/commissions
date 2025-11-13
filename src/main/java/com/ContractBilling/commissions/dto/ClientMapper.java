package com.ContractBilling.commissions.dto;

import com.ContractBilling.commissions.entity.Client;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert between Client entities and DTOs
 */
@Component
public class ClientMapper {

    /**
     * Convert Client entity to ClientResponse DTO
     */
    public ClientResponse toResponse(Client entity) {
        if (entity == null) {
            return null;
        }
        return new ClientResponse(
                entity.getId(),
                entity.getName(),
                entity.getContactPerson(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getAddress(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    /**
     * Convert CreateClientRequest DTO to Client entity
     */
    public Client toEntity(CreateClientRequest request) {
        if (request == null) {
            return null;
        }
        Client entity = new Client();
        entity.setName(request.getName());
        entity.setContactPerson(request.getContactPerson());
        entity.setEmail(request.getEmail());
        entity.setPhone(request.getPhone());
        entity.setAddress(request.getAddress());
        return entity;
    }

    /**
     * Update existing Client entity with data from UpdateClientRequest DTO
     */
    public void updateEntity(Client entity, UpdateClientRequest request) {
        if (entity == null || request == null) {
            return;
        }
        if (request.getName() != null) {
            entity.setName(request.getName());
        }
        if (request.getContactPerson() != null) {
            entity.setContactPerson(request.getContactPerson());
        }
        if (request.getEmail() != null) {
            entity.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            entity.setPhone(request.getPhone());
        }
        if (request.getAddress() != null) {
            entity.setAddress(request.getAddress());
        }
        if (request.getStatus() != null) {
            entity.setStatus(request.getStatus());
        }
    }
}
