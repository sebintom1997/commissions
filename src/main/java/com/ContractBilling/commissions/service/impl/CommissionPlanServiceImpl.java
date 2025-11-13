package com.ContractBilling.commissions.service.impl;

import com.ContractBilling.commissions.dto.CommissionPlanMapper;
import com.ContractBilling.commissions.dto.CommissionPlanResponse;
import com.ContractBilling.commissions.dto.CreateCommissionPlanRequest;
import com.ContractBilling.commissions.dto.UpdateCommissionPlanRequest;
import com.ContractBilling.commissions.entity.CommissionPlan;
import com.ContractBilling.commissions.entity.CommissionPlanStatus;
import com.ContractBilling.commissions.entity.Placement;
import com.ContractBilling.commissions.entity.Salesperson;
import com.ContractBilling.commissions.exception.ResourceNotFoundException;
import com.ContractBilling.commissions.repository.CommissionPlanRepository;
import com.ContractBilling.commissions.repository.PlacementRepository;
import com.ContractBilling.commissions.repository.SalespersonRepository;
import com.ContractBilling.commissions.service.CommissionPlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CommissionPlanServiceImpl implements CommissionPlanService {

    private final CommissionPlanRepository repository;
    private final CommissionPlanMapper mapper;
    private final SalespersonRepository salespersonRepository;
    private final PlacementRepository placementRepository;

    @Override
    public CommissionPlanResponse create(CreateCommissionPlanRequest request) {
        log.info("Creating commission plan for placement ID: {}, salesperson ID: {}",
                request.getPlacementId(), request.getSalespersonId());

        Placement placement = placementRepository.findById(request.getPlacementId())
                .orElseThrow(() -> new ResourceNotFoundException("Placement", "id", request.getPlacementId()));

        Salesperson salesperson = salespersonRepository.findById(request.getSalespersonId())
                .orElseThrow(() -> new ResourceNotFoundException("Salesperson", "id", request.getSalespersonId()));

        CommissionPlan entity = mapper.toEntity(request);
        entity.setPlacement(placement);
        entity.setSalesperson(salesperson);

        if (entity.getConfirmedAmount() == null) {
            entity.setConfirmedAmount(entity.getPlannedAmount());
        }

        CommissionPlan saved = repository.save(entity);
        log.info("Commission plan created with ID: {}", saved.getId());

        return mapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CommissionPlanResponse getById(Long id) {
        CommissionPlan entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CommissionPlan", "id", id));
        return mapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommissionPlanResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommissionPlanResponse> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Override
    public CommissionPlanResponse update(Long id, UpdateCommissionPlanRequest request) {
        log.info("Updating commission plan ID: {}", id);

        CommissionPlan entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CommissionPlan", "id", id));

        mapper.updateEntity(entity, request);
        CommissionPlan updated = repository.save(entity);

        log.info("Commission plan updated: ID: {}", id);
        return mapper.toResponse(updated);
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("CommissionPlan", "id", id);
        }
        repository.deleteById(id);
        log.info("Commission plan deleted: ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommissionPlanResponse> findBySalesperson(Long salespersonId) {
        Salesperson salesperson = salespersonRepository.findById(salespersonId)
                .orElseThrow(() -> new ResourceNotFoundException("Salesperson", "id", salespersonId));

        return repository.findBySalesperson(salesperson).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommissionPlanResponse> findByStatus(CommissionPlanStatus status) {
        return repository.findByStatus(status).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommissionPlanResponse> findBySalespersonAndStatus(Long salespersonId, CommissionPlanStatus status) {
        Salesperson salesperson = salespersonRepository.findById(salespersonId)
                .orElseThrow(() -> new ResourceNotFoundException("Salesperson", "id", salespersonId));

        return repository.findBySalespersonAndStatus(salesperson, status).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CommissionPlanResponse findByPlacement(Long placementId) {
        CommissionPlan entity = repository.findByPlacementId(placementId)
                .orElseThrow(() -> new ResourceNotFoundException("CommissionPlan", "placementId", placementId));
        return mapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getSalespersonTotalPlanned(Long salespersonId) {
        Salesperson salesperson = salespersonRepository.findById(salespersonId)
                .orElseThrow(() -> new ResourceNotFoundException("Salesperson", "id", salespersonId));
        return repository.sumPlannedAmount(salesperson);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getSalespersonTotalRecognized(Long salespersonId) {
        Salesperson salesperson = salespersonRepository.findById(salespersonId)
                .orElseThrow(() -> new ResourceNotFoundException("Salesperson", "id", salespersonId));
        return repository.sumRecognizedAmount(salesperson);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getSalespersonTotalPaid(Long salespersonId) {
        Salesperson salesperson = salespersonRepository.findById(salespersonId)
                .orElseThrow(() -> new ResourceNotFoundException("Salesperson", "id", salespersonId));
        return repository.sumPaidAmount(salesperson);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getSalespersonOutstanding(Long salespersonId) {
        Salesperson salesperson = salespersonRepository.findById(salespersonId)
                .orElseThrow(() -> new ResourceNotFoundException("Salesperson", "id", salespersonId));
        return repository.sumOutstandingAmount(salesperson);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommissionPlanResponse> getEligibleForDrawdown(Long salespersonId) {
        Salesperson salesperson = salespersonRepository.findById(salespersonId)
                .orElseThrow(() -> new ResourceNotFoundException("Salesperson", "id", salespersonId));

        return repository.findEligibleForDrawdown(salesperson).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }
}
