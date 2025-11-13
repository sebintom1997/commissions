package com.ContractBilling.commissions.service;

import com.ContractBilling.commissions.dto.CommissionPlanResponse;
import com.ContractBilling.commissions.dto.CreateCommissionPlanRequest;
import com.ContractBilling.commissions.dto.UpdateCommissionPlanRequest;
import com.ContractBilling.commissions.entity.CommissionPlanStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface CommissionPlanService {

    CommissionPlanResponse create(CreateCommissionPlanRequest request);

    CommissionPlanResponse getById(Long id);

    List<CommissionPlanResponse> getAll();

    Page<CommissionPlanResponse> getAll(Pageable pageable);

    CommissionPlanResponse update(Long id, UpdateCommissionPlanRequest request);

    void delete(Long id);

    List<CommissionPlanResponse> findBySalesperson(Long salespersonId);

    List<CommissionPlanResponse> findByStatus(CommissionPlanStatus status);

    List<CommissionPlanResponse> findBySalespersonAndStatus(Long salespersonId, CommissionPlanStatus status);

    CommissionPlanResponse findByPlacement(Long placementId);

    // Summary methods for dashboard
    BigDecimal getSalespersonTotalPlanned(Long salespersonId);

    BigDecimal getSalespersonTotalRecognized(Long salespersonId);

    BigDecimal getSalespersonTotalPaid(Long salespersonId);

    BigDecimal getSalespersonOutstanding(Long salespersonId);

    List<CommissionPlanResponse> getEligibleForDrawdown(Long salespersonId);
}
