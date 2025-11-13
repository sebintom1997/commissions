package com.ContractBilling.commissions.service;

import com.ContractBilling.commissions.entity.DrawdownRequest;
import com.ContractBilling.commissions.entity.Salesperson;

import java.math.BigDecimal;

/**
 * Business rules engine for drawdown/payout approvals
 */
public interface DrawdownEngine {

    /**
     * Check if salesperson can request drawdown
     */
    boolean canRequestDrawdown(Salesperson salesperson);

    /**
     * Get maximum drawable amount for salesperson
     */
    BigDecimal getMaxDrawableAmount(Salesperson salesperson);

    /**
     * Get available balance (recognized - paid)
     */
    BigDecimal getAvailableBalance(Salesperson salesperson);

    /**
     * Approve drawdown request
     */
    void approveDrawdown(DrawdownRequest request, String approvedBy);

    /**
     * Reject drawdown request
     */
    void rejectDrawdown(DrawdownRequest request, String rejectionReason, String rejectedBy);

    /**
     * Process payment for approved drawdown
     */
    void processPayment(DrawdownRequest request, String paidBy);

    /**
     * Check if max drawdowns per quarter exceeded
     * Returns true if can request more in this quarter
     */
    boolean canRequestInQuarter(Salesperson salesperson, Integer year, Integer quarter);

    /**
     * Get count of approved drawdowns in quarter
     */
    int getDrawdownCountInQuarter(Salesperson salesperson, Integer year, Integer quarter);
}
