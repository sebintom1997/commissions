package com.ContractBilling.commissions.service;

import com.ContractBilling.commissions.dto.CreatePlacementRequest;
import com.ContractBilling.commissions.dto.PlacementResponse;
import com.ContractBilling.commissions.dto.UpdatePlacementRequest;
import com.ContractBilling.commissions.entity.PlacementStatus;
import com.ContractBilling.commissions.entity.PlacementType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for Placement operations
 */
public interface PlacementService {

    /**
     * Create a new placement
     */
    PlacementResponse create(CreatePlacementRequest request);

    /**
     * Get all placements
     */
    List<PlacementResponse> getAll();

    /**
     * Get all placements with pagination
     */
    Page<PlacementResponse> getAll(Pageable pageable);

    /**
     * Get placement by ID
     */
    PlacementResponse getById(Long id);

    /**
     * Update placement
     */
    PlacementResponse update(Long id, UpdatePlacementRequest request);

    /**
     * Delete placement
     */
    void delete(Long id);

    /**
     * Find placements by salesperson ID
     */
    List<PlacementResponse> findBySalespersonId(Long salespersonId);

    /**
     * Find placements by client ID
     */
    List<PlacementResponse> findByClientId(Long clientId);

    /**
     * Find placements by contractor ID
     */
    List<PlacementResponse> findByContractorId(Long contractorId);

    /**
     * Find placements by status
     */
    List<PlacementResponse> findByStatus(PlacementStatus status);

    /**
     * Find placements by type
     */
    List<PlacementResponse> findByType(PlacementType type);
}
