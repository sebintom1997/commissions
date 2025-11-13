package com.ContractBilling.commissions.service;

import com.ContractBilling.commissions.dto.CreateSalespersonRequest;
import com.ContractBilling.commissions.dto.SalespersonResponse;
import com.ContractBilling.commissions.dto.UpdateSalespersonRequest;
import com.ContractBilling.commissions.entity.SalespersonStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SalespersonService {

    // Create a new salesperson
    SalespersonResponse create(CreateSalespersonRequest request);

    // Get all salespeople
    List<SalespersonResponse> getAll();

    // Get all salespeople with pagination and sorting
    Page<SalespersonResponse> getAll(Pageable pageable);

    // Get salesperson by ID
    SalespersonResponse getById(Long id);

    // Update salesperson
    SalespersonResponse update(Long id, UpdateSalespersonRequest request);

    // Delete salesperson
    void delete(Long id);

    // Find by email
    SalespersonResponse findByEmail(String email);

    // Find by status
    List<SalespersonResponse> findByStatus(SalespersonStatus status);

    // Search by name
    List<SalespersonResponse> searchByName(String keyword);
}

