package com.ContractBilling.commissions.repository;

import com.ContractBilling.commissions.entity.Salesperson;
import com.ContractBilling.commissions.entity.SalespersonStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface SalespersonRepository extends JpaRepository<Salesperson, Long> {
    Optional<Salesperson> findByEmail(String email);
    List<Salesperson> findByStatus(SalespersonStatus status);
    boolean existsByEmail(String email);
    List<Salesperson> findByNameContainingIgnoreCase(String name);
}
