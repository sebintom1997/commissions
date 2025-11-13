package com.ContractBilling.commissions.repository;

import com.ContractBilling.commissions.entity.Ledger;
import com.ContractBilling.commissions.entity.LedgerEntryType;
import com.ContractBilling.commissions.entity.Salesperson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LedgerRepository extends JpaRepository<Ledger, Long> {

    List<Ledger> findBySalesperson(Salesperson salesperson);

    List<Ledger> findByEntryType(LedgerEntryType entryType);

    List<Ledger> findBySalespersonAndEntryType(Salesperson salesperson, LedgerEntryType entryType);

    List<Ledger> findByCommissionPlanId(Long commissionPlanId);

    List<Ledger> findByPlacementId(Long placementId);

    // Total by entry type for salesperson
    @Query("SELECT COALESCE(SUM(l.amount), 0) FROM Ledger l " +
           "WHERE l.salesperson = :salesperson AND l.entryType = :entryType")
    BigDecimal sumByType(@Param("salesperson") Salesperson salesperson,
                         @Param("entryType") LedgerEntryType entryType);

    // Total accrued
    @Query("SELECT COALESCE(SUM(l.amount), 0) FROM Ledger l " +
           "WHERE l.salesperson = :salesperson AND l.entryType = 'COMMISSION_ACCRUED'")
    BigDecimal sumAccrued(@Param("salesperson") Salesperson salesperson);

    // Total recognized
    @Query("SELECT COALESCE(SUM(l.amount), 0) FROM Ledger l " +
           "WHERE l.salesperson = :salesperson AND l.entryType = 'COMMISSION_RECOGNIZED'")
    BigDecimal sumRecognized(@Param("salesperson") Salesperson salesperson);

    // Total paid
    @Query("SELECT COALESCE(SUM(l.amount), 0) FROM Ledger l " +
           "WHERE l.salesperson = :salesperson AND l.entryType = 'COMMISSION_PAID'")
    BigDecimal sumPaid(@Param("salesperson") Salesperson salesperson);

    // Entries in date range
    @Query("SELECT l FROM Ledger l WHERE l.salesperson = :salesperson " +
           "AND l.createdAt >= :startDate AND l.createdAt <= :endDate " +
           "ORDER BY l.createdAt DESC")
    List<Ledger> findByDateRange(@Param("salesperson") Salesperson salesperson,
                                 @Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate);
}
