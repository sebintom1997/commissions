package com.ContractBilling.commissions.service.impl;

import com.ContractBilling.commissions.dto.ClientMapper;
import com.ContractBilling.commissions.dto.ClientResponse;
import com.ContractBilling.commissions.dto.CreateClientRequest;
import com.ContractBilling.commissions.dto.UpdateClientRequest;
import com.ContractBilling.commissions.entity.Client;
import com.ContractBilling.commissions.entity.ClientStatus;
import com.ContractBilling.commissions.exception.DuplicateResourceException;
import com.ContractBilling.commissions.exception.ResourceNotFoundException;
import com.ContractBilling.commissions.repository.ClientRepository;
import com.ContractBilling.commissions.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of ClientService
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ClientServiceImpl implements ClientService {

    private final ClientRepository repository;
    private final ClientMapper mapper;

    @Override
    public ClientResponse create(CreateClientRequest request) {
        log.info("Creating client with name: {}", request.getName());

        // Business Rule: Check for duplicate name
        if (repository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Client", "name", request.getName());
        }

        // Business Rule: Check for duplicate email if provided
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            if (repository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("Client", "email", request.getEmail());
            }
        }

        // Convert DTO → Entity
        Client entity = mapper.toEntity(request);

        // Save to database
        Client saved = repository.save(entity);

        log.info("Client created successfully with ID: {}", saved.getId());

        // Convert Entity → DTO and return
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientResponse> getAll() {
        log.info("Fetching all clients");

        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClientResponse> getAll(Pageable pageable) {
        log.info("Fetching clients with pagination: page={}, size={}, sort={}",
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        return repository.findAll(pageable)
                .map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientResponse getById(Long id) {
        log.info("Fetching client with ID: {}", id);

        Client entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", id));

        return mapper.toResponse(entity);
    }

    @Override
    public ClientResponse update(Long id, UpdateClientRequest request) {
        log.info("Updating client with ID: {}", id);

        // Find existing entity
        Client entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", id));

        // Business Rule: If changing name, check for duplicates
        if (request.getName() != null && !request.getName().equals(entity.getName())) {
            if (repository.existsByName(request.getName())) {
                throw new DuplicateResourceException("Client", "name", request.getName());
            }
        }

        // Business Rule: If changing email, check for duplicates
        if (request.getEmail() != null && !request.getEmail().equals(entity.getEmail())) {
            if (repository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("Client", "email", request.getEmail());
            }
        }

        // Update entity fields
        mapper.updateEntity(entity, request);

        // Save (updatedAt is set automatically by @PreUpdate)
        Client updated = repository.save(entity);

        log.info("Client updated successfully with ID: {}", id);

        return mapper.toResponse(updated);
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting client with ID: {}", id);

        // Check if exists
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Client", "id", id);
        }

        repository.deleteById(id);

        log.info("Client deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientResponse findByEmail(String email) {
        log.info("Finding client by email: {}", email);

        Client entity = repository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "email", email));

        return mapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientResponse> findByStatus(ClientStatus status) {
        log.info("Finding clients by status: {}", status);

        return repository.findByStatus(status)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientResponse> searchByName(String keyword) {
        log.info("Searching clients by name keyword: {}", keyword);

        return repository.findByNameContainingIgnoreCase(keyword)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }
}
