package com.ContractBilling.commissions.service;

import com.ContractBilling.commissions.dto.ContractorResponse;
import com.ContractBilling.commissions.dto.CreateContractorRequest;
import com.ContractBilling.commissions.dto.UpdateContractorRequest;
import com.ContractBilling.commissions.entity.ContractorStatus;
import com.ContractBilling.commissions.entity.ContractorType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for Contractor operations
 */
public interface ContractorService {

    /**
     * Create a new contractor
     */
    ContractorResponse create(CreateContractorRequest request);

    /**
     * Get all contractors
     */
    List<ContractorResponse> getAll();

    /**
     * Get all contractors with pagination and sorting
     */
    Page<ContractorResponse> getAll(Pageable pageable);

    /**
     * Get contractor by ID
     */
    ContractorResponse getById(Long id);

    /**
     * Update contractor
     */
    ContractorResponse update(Long id, UpdateContractorRequest request);

    /**
     * Delete contractor
     */
    void delete(Long id);

    /**
     * Find contractor by email
     */
    ContractorResponse findByEmail(String email);

    /**
     * Find contractors by status
     */
    List<ContractorResponse> findByStatus(ContractorStatus status);

    /**
     * Find contractors by type
     */
    List<ContractorResponse> findByType(ContractorType type);

    /**
     * Search contractors by name
     */
    List<ContractorResponse> searchByName(String keyword);
}
