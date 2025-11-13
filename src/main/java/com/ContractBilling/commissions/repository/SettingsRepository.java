package com.ContractBilling.commissions.repository;

import com.ContractBilling.commissions.entity.PolicySettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for PolicySettings entity
 * This is a singleton - only ONE record should exist
 */
@Repository
public interface SettingsRepository extends JpaRepository<PolicySettings, Long> {
    // No custom methods needed - we'll always fetch the first (and only) record
}
