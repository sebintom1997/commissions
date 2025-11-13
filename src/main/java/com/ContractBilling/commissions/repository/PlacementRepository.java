package com.ContractBilling.commissions.repository;

import com.ContractBilling.commissions.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Placement entity
 */
@Repository
public interface PlacementRepository extends JpaRepository<Placement, Long> {

    /**
     * Find all placements by salesperson
     */
    List<Placement> findBySalesperson(Salesperson salesperson);

    /**
     * Find all placements by client
     */
    List<Placement> findByClient(Client client);

    /**
     * Find all placements by contractor
     */
    List<Placement> findByContractor(Contractor contractor);

    /**
     * Find all placements by status
     */
    List<Placement> findByStatus(PlacementStatus status);

    /**
     * Find all placements by type
     */
    List<Placement> findByPlacementType(PlacementType placementType);

    /**
     * Count placements by contractor and client (for sequence number calculation)
     * This helps determine if this is the 1st, 2nd, 3rd contract
     */
    @Query("SELECT COUNT(p) FROM Placement p WHERE p.contractor = :contractor AND p.client = :client")
    int countByContractorAndClient(@Param("contractor") Contractor contractor, @Param("client") Client client);

    /**
     * Find active placements for a salesperson
     */
    @Query("SELECT p FROM Placement p WHERE p.salesperson = :salesperson AND p.status = 'ACTIVE'")
    List<Placement> findActivePlacementsBySalesperson(@Param("salesperson") Salesperson salesperson);
}
