package com.ContractBilling.commissions.service;

import com.ContractBilling.commissions.dto.ClientResponse;
import com.ContractBilling.commissions.dto.CreateClientRequest;
import com.ContractBilling.commissions.dto.UpdateClientRequest;
import com.ContractBilling.commissions.entity.ClientStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for Client operations
 */
public interface ClientService {

    /**
     * Create a new client
     */
    ClientResponse create(CreateClientRequest request);

    /**
     * Get all clients
     */
    List<ClientResponse> getAll();

    /**
     * Get all clients with pagination and sorting
     */
    Page<ClientResponse> getAll(Pageable pageable);

    /**
     * Get client by ID
     */
    ClientResponse getById(Long id);

    /**
     * Update client
     */
    ClientResponse update(Long id, UpdateClientRequest request);

    /**
     * Delete client
     */
    void delete(Long id);

    /**
     * Find client by email
     */
    ClientResponse findByEmail(String email);

    /**
     * Find clients by status
     */
    List<ClientResponse> findByStatus(ClientStatus status);

    /**
     * Search clients by name
     */
    List<ClientResponse> searchByName(String keyword);
}
