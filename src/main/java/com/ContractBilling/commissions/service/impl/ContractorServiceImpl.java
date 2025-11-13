package com.ContractBilling.commissions.service.impl;

import com.ContractBilling.commissions.dto.ContractorMapper;
import com.ContractBilling.commissions.dto.ContractorResponse;
import com.ContractBilling.commissions.dto.CreateContractorRequest;
import com.ContractBilling.commissions.dto.UpdateContractorRequest;
import com.ContractBilling.commissions.entity.Contractor;
import com.ContractBilling.commissions.entity.ContractorStatus;
import com.ContractBilling.commissions.entity.ContractorType;
import com.ContractBilling.commissions.exception.DuplicateResourceException;
import com.ContractBilling.commissions.exception.ResourceNotFoundException;
import com.ContractBilling.commissions.repository.ContractorRepository;
import com.ContractBilling.commissions.service.ContractorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of ContractorService
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ContractorServiceImpl implements ContractorService {

    private final ContractorRepository repository;
    private final ContractorMapper mapper;

    @Override
    public ContractorResponse create(CreateContractorRequest request) {
        log.info("Creating contractor with name: {}", request.getName());

        // Business Rule: Check for duplicate email if provided
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            if (repository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("Contractor", "email", request.getEmail());
            }
        }

        // Convert DTO → Entity
        Contractor entity = mapper.toEntity(request);

        // Save to database
        Contractor saved = repository.save(entity);

        log.info("Contractor created successfully with ID: {}", saved.getId());

        // Convert Entity → DTO and return
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContractorResponse> getAll() {
        log.info("Fetching all contractors");

        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContractorResponse> getAll(Pageable pageable) {
        log.info("Fetching contractors with pagination: page={}, size={}, sort={}",
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        return repository.findAll(pageable)
                .map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ContractorResponse getById(Long id) {
        log.info("Fetching contractor with ID: {}", id);

        Contractor entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contractor", "id", id));

        return mapper.toResponse(entity);
    }

    @Override
    public ContractorResponse update(Long id, UpdateContractorRequest request) {
        log.info("Updating contractor with ID: {}", id);

        // Find existing entity
        Contractor entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contractor", "id", id));

        // Business Rule: If changing email, check for duplicates
        if (request.getEmail() != null && !request.getEmail().equals(entity.getEmail())) {
            if (repository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("Contractor", "email", request.getEmail());
            }
        }

        // Update entity fields
        mapper.updateEntity(entity, request);

        // Save (updatedAt is set automatically by @PreUpdate)
        Contractor updated = repository.save(entity);

        log.info("Contractor updated successfully with ID: {}", id);

        return mapper.toResponse(updated);
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting contractor with ID: {}", id);

        // Check if exists
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Contractor", "id", id);
        }

        repository.deleteById(id);

        log.info("Contractor deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public ContractorResponse findByEmail(String email) {
        log.info("Finding contractor by email: {}", email);

        Contractor entity = repository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Contractor", "email", email));

        return mapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContractorResponse> findByStatus(ContractorStatus status) {
        log.info("Finding contractors by status: {}", status);

        return repository.findByStatus(status)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContractorResponse> findByType(ContractorType type) {
        log.info("Finding contractors by type: {}", type);

        return repository.findByType(type)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContractorResponse> searchByName(String keyword) {
        log.info("Searching contractors by name keyword: {}", keyword);

        return repository.findByNameContainingIgnoreCase(keyword)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }
}
