package com.ContractBilling.commissions.service.impl;

import com.ContractBilling.commissions.dto.CreateSalespersonRequest;
import com.ContractBilling.commissions.dto.SalespersonMapper;
import com.ContractBilling.commissions.dto.SalespersonResponse;
import com.ContractBilling.commissions.dto.UpdateSalespersonRequest;
import com.ContractBilling.commissions.entity.Salesperson;
import com.ContractBilling.commissions.entity.SalespersonStatus;
import com.ContractBilling.commissions.exception.DuplicateResourceException;
import com.ContractBilling.commissions.exception.ResourceNotFoundException;
import com.ContractBilling.commissions.repository.SalespersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ContractBilling.commissions.service.SalespersonService;
import java.util.List;
import java.util.stream.Collectors;

@Service  // Tells Spring: this is a service bean
@RequiredArgsConstructor  // Lombok: generates constructor for final fields
@Slf4j  // Lombok: generates logger
@Transactional  // All methods run in database transactions
public class SalespersonServiceImpl implements SalespersonService {

    // Dependencies (injected by Spring via constructor)
    private final SalespersonRepository repository;
    private final SalespersonMapper mapper;

    @Override
    public SalespersonResponse create(CreateSalespersonRequest request) {
        log.info("Creating salesperson with email: {}", request.getEmail());

        // Business Rule: Check for duplicate email
        if (repository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Salesperson", "email", request.getEmail());
        }

        // Convert DTO → Entity
        Salesperson entity = mapper.toEntity(request);

        // Save to database
        Salesperson saved = repository.save(entity);

        log.info("Salesperson created successfully with ID: {}", saved.getId());

        // Convert Entity → DTO and return
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)  // Optimization for read operations
    public List<SalespersonResponse> getAll() {
        log.info("Fetching all salespeople");

        return repository.findAll()
                .stream()
                .map(mapper::toResponse)  // Convert each entity to DTO
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SalespersonResponse> getAll(Pageable pageable) {
        log.info("Fetching salespeople with pagination: page={}, size={}, sort={}",
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        return repository.findAll(pageable)
                .map(mapper::toResponse);  // Convert each entity to DTO
    }

    @Override
    @Transactional(readOnly = true)
    public SalespersonResponse getById(Long id) {
        log.info("Fetching salesperson with ID: {}", id);

        Salesperson entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salesperson", "id", id));

        return mapper.toResponse(entity);
    }

    @Override
    public SalespersonResponse update(Long id, UpdateSalespersonRequest request) {
        log.info("Updating salesperson with ID: {}", id);

        // Find existing entity
        Salesperson entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salesperson", "id", id));

        // Business Rule: If changing email, check for duplicates
        if (request.getEmail() != null && !request.getEmail().equals(entity.getEmail())) {
            if (repository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("Salesperson", "email", request.getEmail());
            }
        }

        // Update entity fields
        mapper.updateEntity(entity, request);

        // Save (updatedAt is set automatically by @PreUpdate)
        Salesperson updated = repository.save(entity);

        log.info("Salesperson updated successfully with ID: {}", id);

        return mapper.toResponse(updated);
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting salesperson with ID: {}", id);

        // Check if exists
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Salesperson", "id", id);
        }

        repository.deleteById(id);

        log.info("Salesperson deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public SalespersonResponse findByEmail(String email) {
        log.info("Finding salesperson by email: {}", email);

        Salesperson entity = repository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Salesperson", "email", email));

        return mapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalespersonResponse> findByStatus(SalespersonStatus status) {
        log.info("Finding salespeople by status: {}", status);

        return repository.findByStatus(status)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalespersonResponse> searchByName(String keyword) {
        log.info("Searching salespeople by name keyword: {}", keyword);

        return repository.findByNameContainingIgnoreCase(keyword)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }
}