package com.ContractBilling.commissions.repository;

import com.ContractBilling.commissions.entity.Contractor;
import com.ContractBilling.commissions.entity.ContractorStatus;
import com.ContractBilling.commissions.entity.ContractorType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Contractor entity
 * Spring Data JPA automatically generates implementations
 */
@Repository
public interface ContractorRepository extends JpaRepository<Contractor, Long> {

    /**
     * Find contractor by email
     */
    Optional<Contractor> findByEmail(String email);

    /**
     * Check if contractor exists with given email
     */
    boolean existsByEmail(String email);

    /**
     * Find all contractors by status
     */
    List<Contractor> findByStatus(ContractorStatus status);

    /**
     * Find all contractors by type
     */
    List<Contractor> findByType(ContractorType type);

    /**
     * Search contractors by name (case-insensitive, partial match)
     */
    List<Contractor> findByNameContainingIgnoreCase(String name);
}
