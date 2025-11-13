package com.ContractBilling.commissions.service.impl;

import com.ContractBilling.commissions.dto.CreateCommissionPlanRequest;
import com.ContractBilling.commissions.dto.CreatePlacementRequest;
import com.ContractBilling.commissions.dto.PlacementMapper;
import com.ContractBilling.commissions.dto.PlacementResponse;
import com.ContractBilling.commissions.dto.UpdatePlacementRequest;
import com.ContractBilling.commissions.entity.*;
import com.ContractBilling.commissions.exception.ResourceNotFoundException;
import com.ContractBilling.commissions.repository.*;
import com.ContractBilling.commissions.service.CommissionCalculationService;
import com.ContractBilling.commissions.service.CommissionPlanService;
import com.ContractBilling.commissions.service.PlacementService;
import com.ContractBilling.commissions.service.RevenueRecognitionEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of PlacementService
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PlacementServiceImpl implements PlacementService {

    private final PlacementRepository repository;
    private final PlacementMapper mapper;
    private final SalespersonRepository salespersonRepository;
    private final ClientRepository clientRepository;
    private final ContractorRepository contractorRepository;
    private final SettingsRepository settingsRepository;
    private final CommissionCalculationService calculationService;
    private final CommissionPlanService commissionPlanService;
    private final CommissionPlanRepository commissionPlanRepository;
    private final RevenueRecognitionEngine recognitionEngine;

    @Override
    public PlacementResponse create(CreatePlacementRequest request) {
        log.info("Creating placement for contractor ID: {}, client ID: {}",
                request.getContractorId(), request.getClientId());

        // Fetch related entities
        Salesperson salesperson = salespersonRepository.findById(request.getSalespersonId())
                .orElseThrow(() -> new ResourceNotFoundException("Salesperson", "id", request.getSalespersonId()));

        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", request.getClientId()));

        Contractor contractor = contractorRepository.findById(request.getContractorId())
                .orElseThrow(() -> new ResourceNotFoundException("Contractor", "id", request.getContractorId()));

        // Get current settings for defaults
        List<PolicySettings> allSettings = settingsRepository.findAll();
        PolicySettings settings = allSettings.isEmpty() ? null : allSettings.get(0);

        // Convert DTO → Entity
        Placement entity = mapper.toEntity(request);

        // Set relationships
        entity.setSalesperson(salesperson);
        entity.setClient(client);
        entity.setContractor(contractor);

        // Set default overhead percentages from settings
        if (settings != null && entity.getPlacementType() == PlacementType.CONTRACTOR) {
            entity.setAdminPercentage(settings.getAdminPercentage());
            entity.setInsurancePercentage(settings.getInsurancePercentage());
        }

        // Calculate sequence number (how many times this contractor placed at this client)
        int sequenceNumber = repository.countByContractorAndClient(contractor, client) + 1;
        entity.setSequenceNumber(sequenceNumber);

        // Calculate all financial fields and commission
        if (entity.getPlacementType() == PlacementType.CONTRACTOR) {
            calculationService.calculateContractorCommission(entity, settings);
        } else if (entity.getPlacementType() == PlacementType.PERMANENT) {
            calculationService.calculatePermanentCommission(entity, settings);
        }

        // Save to database
        Placement saved = repository.save(entity);

        log.info("Placement created successfully with ID: {}", saved.getId());

        // Create commission plan automatically
        CreateCommissionPlanRequest planRequest = CreateCommissionPlanRequest.builder()
                .placementId(saved.getId())
                .salespersonId(salesperson.getId())
                .plannedAmount(saved.getCommissionTotal())
                .confirmedAmount(saved.getCommissionTotal())
                .status(CommissionPlanStatus.PLANNED)
                .recognitionStartDate(saved.getStartDate())
                .monthsToRecognize(12)
                .eligibleForDrawdown(false)
                .notes("Auto-created from placement")
                .build();

        var plan = commissionPlanService.create(planRequest);
        log.info("Commission plan created: ID={}", plan.getId());

        // Generate recognition schedule
        // Fetch the created plan from database for recognition engine
        CommissionPlan savedPlan = commissionPlanRepository.findById(plan.getId())
                .orElseThrow(() -> new ResourceNotFoundException("CommissionPlan", "id", plan.getId()));
        recognitionEngine.generateRecognitionSchedule(savedPlan);
        log.info("Recognition schedule generated for plan ID: {}", plan.getId());

        // Convert Entity → DTO and return
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlacementResponse> getAll() {
        log.info("Fetching all placements");

        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PlacementResponse> getAll(Pageable pageable) {
        log.info("Fetching placements with pagination: page={}, size={}, sort={}",
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        return repository.findAll(pageable)
                .map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PlacementResponse getById(Long id) {
        log.info("Fetching placement with ID: {}", id);

        Placement entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Placement", "id", id));

        return mapper.toResponse(entity);
    }

    @Override
    public PlacementResponse update(Long id, UpdatePlacementRequest request) {
        log.info("Updating placement with ID: {}", id);

        // Find existing entity
        Placement entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Placement", "id", id));

        // Update entity fields
        mapper.updateEntity(entity, request);

        // Recalculate if financial fields changed
        boolean shouldRecalculate = request.getAnnualSalary() != null
                || request.getHourlyPayRate() != null
                || request.getBillRate() != null
                || request.getHoursPerWeek() != null
                || request.getWeeksPerYear() != null
                || request.getPlacementFee() != null;

        if (shouldRecalculate) {
            // Get current settings
            List<PolicySettings> allSettings = settingsRepository.findAll();
            PolicySettings settings = allSettings.isEmpty() ? null : allSettings.get(0);

            if (settings != null) {
                if (entity.getPlacementType() == PlacementType.CONTRACTOR) {
                    calculationService.calculateContractorCommission(entity, settings);
                } else if (entity.getPlacementType() == PlacementType.PERMANENT) {
                    calculationService.calculatePermanentCommission(entity, settings);
                }
            }
        }

        // Save
        Placement updated = repository.save(entity);

        log.info("Placement updated successfully with ID: {}", id);

        return mapper.toResponse(updated);
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting placement with ID: {}", id);

        // Check if exists
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Placement", "id", id);
        }

        repository.deleteById(id);

        log.info("Placement deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlacementResponse> findBySalespersonId(Long salespersonId) {
        log.info("Finding placements by salesperson ID: {}", salespersonId);

        Salesperson salesperson = salespersonRepository.findById(salespersonId)
                .orElseThrow(() -> new ResourceNotFoundException("Salesperson", "id", salespersonId));

        return repository.findBySalesperson(salesperson)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlacementResponse> findByClientId(Long clientId) {
        log.info("Finding placements by client ID: {}", clientId);

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", clientId));

        return repository.findByClient(client)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlacementResponse> findByContractorId(Long contractorId) {
        log.info("Finding placements by contractor ID: {}", contractorId);

        Contractor contractor = contractorRepository.findById(contractorId)
                .orElseThrow(() -> new ResourceNotFoundException("Contractor", "id", contractorId));

        return repository.findByContractor(contractor)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlacementResponse> findByStatus(PlacementStatus status) {
        log.info("Finding placements by status: {}", status);

        return repository.findByStatus(status)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlacementResponse> findByType(PlacementType type) {
        log.info("Finding placements by type: {}", type);

        return repository.findByPlacementType(type)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }
}
