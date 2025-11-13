package com.ContractBilling.commissions.entity;

/**
 * Status of a commission plan
 */
public enum CommissionPlanStatus {
    PLANNED,      // Initial estimate
    CONFIRMED,    // Amount confirmed
    RECOGNIZED,   // Revenue recognized
    PAID,         // Paid out
    REVERSED      // Reversed/Cancelled
}
