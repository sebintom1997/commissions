package com.ContractBilling.commissions.entity;

public enum LedgerEntryType {
    COMMISSION_ACCRUED,     // Initial commission accrual
    COMMISSION_ADJUSTED,    // Adjustment to commission
    COMMISSION_RECOGNIZED,  // Revenue recognized
    COMMISSION_PAID,        // Payout made
    DRAWDOWN_REQUESTED,     // Drawdown request
    DRAWDOWN_APPROVED,      // Drawdown approved
    DRAWDOWN_REJECTED,      // Drawdown rejected
    REVERSAL,               // Reversal/Cancellation
    ADJUSTMENT              // Other adjustments
}
