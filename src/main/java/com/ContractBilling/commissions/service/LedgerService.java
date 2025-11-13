package com.ContractBilling.commissions.service;

import com.ContractBilling.commissions.dto.LedgerResponse;
import com.ContractBilling.commissions.entity.LedgerEntryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface LedgerService {

    LedgerResponse getById(Long id);

    List<LedgerResponse> getAll();

    Page<LedgerResponse> getAll(Pageable pageable);

    List<LedgerResponse> findBySalesperson(Long salespersonId);

    List<LedgerResponse> findByType(LedgerEntryType type);

    List<LedgerResponse> findBySalespersonAndType(Long salespersonId, LedgerEntryType type);

    List<LedgerResponse> findByCommissionPlan(Long commissionPlanId);

    List<LedgerResponse> findByPlacement(Long placementId);

    List<LedgerResponse> findByDateRange(Long salespersonId, LocalDateTime startDate, LocalDateTime endDate);

    // Summary methods
    BigDecimal getSalespersonTotalAccrued(Long salespersonId);

    BigDecimal getSalespersonTotalRecognized(Long salespersonId);

    BigDecimal getSalespersonTotalPaid(Long salespersonId);

    // Internal methods for creating ledger entries
    void recordCommissionAccrual(Long commissionPlanId, Long salespersonId, BigDecimal amount, String description);

    void recordCommissionRecognized(Long commissionPlanId, Long salespersonId, BigDecimal amount, String description);

    void recordCommissionPaid(Long commissionPlanId, Long salespersonId, BigDecimal amount, String description);

    void recordAdjustment(Long salespersonId, BigDecimal amount, String description);
}
