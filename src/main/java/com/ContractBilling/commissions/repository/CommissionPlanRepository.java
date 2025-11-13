package com.ContractBilling.commissions.repository;

import com.ContractBilling.commissions.entity.CommissionPlan;
import com.ContractBilling.commissions.entity.CommissionPlanStatus;
import com.ContractBilling.commissions.entity.Salesperson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommissionPlanRepository extends JpaRepository<CommissionPlan, Long> {

    List<CommissionPlan> findBySalesperson(Salesperson salesperson);

    List<CommissionPlan> findByStatus(CommissionPlanStatus status);

    List<CommissionPlan> findBySalespersonAndStatus(Salesperson salesperson, CommissionPlanStatus status);

    Optional<CommissionPlan> findByPlacementId(Long placementId);

    // Total planned commission for salesperson
    @Query("SELECT COALESCE(SUM(cp.plannedAmount), 0) FROM CommissionPlan cp " +
           "WHERE cp.salesperson = :salesperson")
    BigDecimal sumPlannedAmount(@Param("salesperson") Salesperson salesperson);

    // Total recognized commission for salesperson
    @Query("SELECT COALESCE(SUM(cp.recognizedAmount), 0) FROM CommissionPlan cp " +
           "WHERE cp.salesperson = :salesperson")
    BigDecimal sumRecognizedAmount(@Param("salesperson") Salesperson salesperson);

    // Total paid commission for salesperson
    @Query("SELECT COALESCE(SUM(cp.paidAmount), 0) FROM CommissionPlan cp " +
           "WHERE cp.salesperson = :salesperson")
    BigDecimal sumPaidAmount(@Param("salesperson") Salesperson salesperson);

    // Outstanding (recognized but not paid)
    @Query("SELECT COALESCE(SUM(cp.recognizedAmount - cp.paidAmount), 0) FROM CommissionPlan cp " +
           "WHERE cp.salesperson = :salesperson AND cp.recognizedAmount > cp.paidAmount")
    BigDecimal sumOutstandingAmount(@Param("salesperson") Salesperson salesperson);

    // Eligible for drawdown
    @Query("SELECT cp FROM CommissionPlan cp " +
           "WHERE cp.salesperson = :salesperson AND cp.eligibleForDrawdown = true " +
           "AND cp.status IN ('RECOGNIZED', 'PAID')")
    List<CommissionPlan> findEligibleForDrawdown(@Param("salesperson") Salesperson salesperson);
}
