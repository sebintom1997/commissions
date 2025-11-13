package com.ContractBilling.commissions.entity;

/**
 * Status of a placement
 */
public enum PlacementStatus {
    DRAFT,       // Draft placement (not yet active)
    ACTIVE,      // Currently active placement
    COMPLETED,   // Placement completed successfully
    TERMINATED   // Placement terminated early
}
