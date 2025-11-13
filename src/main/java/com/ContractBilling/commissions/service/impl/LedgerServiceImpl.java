package com.ContractBilling.commissions.service.impl;

import com.ContractBilling.commissions.dto.LedgerMapper;
import com.ContractBilling.commissions.dto.LedgerResponse;
import com.ContractBilling.commissions.entity.*;
import com.ContractBilling.commissions.exception.ResourceNotFoundException;
import com.ContractBilling.commissions.repository.CommissionPlanRepository;
import com.ContractBilling.commissions.repository.LedgerRepository;
import com.ContractBilling.commissions.repository.SalespersonRepository;
import com.ContractBilling.commissions.service.LedgerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LedgerServiceImpl implements LedgerService {

    private final LedgerRepository repository;
    private final LedgerMapper mapper;
    private final SalespersonRepository salespersonRepository;
    private final CommissionPlanRepository commissionPlanRepository;

    @Override
    @Transactional(readOnly = true)
    public LedgerResponse getById(Long id) {
        Ledger entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ledger", "id", id));
        return mapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LedgerResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LedgerResponse> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LedgerResponse> findBySalesperson(Long salespersonId) {
        Salesperson salesperson = salespersonRepository.findById(salespersonId)
                .orElseThrow(() -> new ResourceNotFoundException("Salesperson", "id", salespersonId));

        return repository.findBySalesperson(salesperson).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LedgerResponse> findByType(LedgerEntryType type) {
        return repository.findByEntryType(type).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LedgerResponse> findBySalespersonAndType(Long salespersonId, LedgerEntryType type) {
        Salesperson salesperson = salespersonRepository.findById(salespersonId)
                .orElseThrow(() -> new ResourceNotFoundException("Salesperson", "id", salespersonId));

        return repository.findBySalespersonAndEntryType(salesperson, type).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LedgerResponse> findByCommissionPlan(Long commissionPlanId) {
        return repository.findByCommissionPlanId(commissionPlanId).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LedgerResponse> findByPlacement(Long placementId) {
        return repository.findByPlacementId(placementId).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LedgerResponse> findByDateRange(Long salespersonId, LocalDateTime startDate, LocalDateTime endDate) {
        Salesperson salesperson = salespersonRepository.findById(salespersonId)
                .orElseThrow(() -> new ResourceNotFoundException("Salesperson", "id", salespersonId));

        return repository.findByDateRange(salesperson, startDate, endDate).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getSalespersonTotalAccrued(Long salespersonId) {
        Salesperson salesperson = salespersonRepository.findById(salespersonId)
                .orElseThrow(() -> new ResourceNotFoundException("Salesperson", "id", salespersonId));
        return repository.sumAccrued(salesperson);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getSalespersonTotalRecognized(Long salespersonId) {
        Salesperson salesperson = salespersonRepository.findById(salespersonId)
                .orElseThrow(() -> new ResourceNotFoundException("Salesperson", "id", salespersonId));
        return repository.sumRecognized(salesperson);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getSalespersonTotalPaid(Long salespersonId) {
        Salesperson salesperson = salespersonRepository.findById(salespersonId)
                .orElseThrow(() -> new ResourceNotFoundException("Salesperson", "id", salespersonId));
        return repository.sumPaid(salesperson);
    }

    @Override
    public void recordCommissionAccrual(Long commissionPlanId, Long salespersonId, BigDecimal amount, String description) {
        CommissionPlan plan = commissionPlanRepository.findById(commissionPlanId)
                .orElseThrow(() -> new ResourceNotFoundException("CommissionPlan", "id", commissionPlanId));

        Salesperson salesperson = salespersonRepository.findById(salespersonId)
                .orElseThrow(() -> new ResourceNotFoundException("Salesperson", "id", salespersonId));

        Ledger entry = Ledger.builder()
                .commissionPlan(plan)
                .salesperson(salesperson)
                .placement(plan.getPlacement())
                .entryType(LedgerEntryType.COMMISSION_ACCRUED)
                .amount(amount)
                .description(description)
                .referenceType("PLACEMENT")
                .referenceId(plan.getPlacement().getId())
                .status("COMPLETED")
                .build();

        repository.save(entry);
        log.info("Commission accrual recorded: CommissionPlan={}, Amount={}", commissionPlanId, amount);
    }

    @Override
    public void recordCommissionRecognized(Long commissionPlanId, Long salespersonId, BigDecimal amount, String description) {
        CommissionPlan plan = commissionPlanRepository.findById(commissionPlanId)
                .orElseThrow(() -> new ResourceNotFoundException("CommissionPlan", "id", commissionPlanId));

        Salesperson salesperson = salespersonRepository.findById(salespersonId)
                .orElseThrow(() -> new ResourceNotFoundException("Salesperson", "id", salespersonId));

        Ledger entry = Ledger.builder()
                .commissionPlan(plan)
                .salesperson(salesperson)
                .placement(plan.getPlacement())
                .entryType(LedgerEntryType.COMMISSION_RECOGNIZED)
                .amount(amount)
                .description(description)
                .referenceType("PLACEMENT")
                .referenceId(plan.getPlacement().getId())
                .status("COMPLETED")
                .build();

        repository.save(entry);
        log.info("Commission recognized: CommissionPlan={}, Amount={}", commissionPlanId, amount);
    }

    @Override
    public void recordCommissionPaid(Long commissionPlanId, Long salespersonId, BigDecimal amount, String description) {
        CommissionPlan plan = commissionPlanRepository.findById(commissionPlanId)
                .orElseThrow(() -> new ResourceNotFoundException("CommissionPlan", "id", commissionPlanId));

        Salesperson salesperson = salespersonRepository.findById(salespersonId)
                .orElseThrow(() -> new ResourceNotFoundException("Salesperson", "id", salespersonId));

        Ledger entry = Ledger.builder()
                .commissionPlan(plan)
                .salesperson(salesperson)
                .placement(plan.getPlacement())
                .entryType(LedgerEntryType.COMMISSION_PAID)
                .amount(amount)
                .description(description)
                .referenceType("PLACEMENT")
                .referenceId(plan.getPlacement().getId())
                .status("COMPLETED")
                .build();

        repository.save(entry);
        log.info("Commission paid: CommissionPlan={}, Amount={}", commissionPlanId, amount);
    }

    @Override
    public void recordAdjustment(Long salespersonId, BigDecimal amount, String description) {
        Salesperson salesperson = salespersonRepository.findById(salespersonId)
                .orElseThrow(() -> new ResourceNotFoundException("Salesperson", "id", salespersonId));

        Ledger entry = Ledger.builder()
                .salesperson(salesperson)
                .entryType(LedgerEntryType.ADJUSTMENT)
                .amount(amount)
                .description(description)
                .status("COMPLETED")
                .build();

        repository.save(entry);
        log.info("Ledger adjustment recorded: Salesperson={}, Amount={}", salespersonId, amount);
    }
}
