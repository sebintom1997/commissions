package com.ContractBilling.commissions.repository;

import com.ContractBilling.commissions.entity.Client;
import com.ContractBilling.commissions.entity.ClientStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Client entity
 * Spring Data JPA automatically generates implementations for these methods
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    /**
     * Find client by email
     */
    Optional<Client> findByEmail(String email);

    /**
     * Find all clients by status
     */
    List<Client> findByStatus(ClientStatus status);

    /**
     * Check if client exists with given name
     */
    boolean existsByName(String name);

    /**
     * Check if client exists with given email
     */
    boolean existsByEmail(String email);

    /**
     * Search clients by name (case-insensitive, partial match)
     */
    List<Client> findByNameContainingIgnoreCase(String name);
}
