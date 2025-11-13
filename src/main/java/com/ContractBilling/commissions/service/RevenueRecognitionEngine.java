package com.ContractBilling.commissions.service;

import com.ContractBilling.commissions.entity.CommissionPlan;
import com.ContractBilling.commissions.entity.RecognitionSchedule;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Engine for revenue recognition calculations and scheduling
 */
public interface RevenueRecognitionEngine {

    /**
     * Generate recognition schedule for a commission plan
     * Creates monthly entries based on recognition period
     */
    List<RecognitionSchedule> generateRecognitionSchedule(CommissionPlan plan);

    /**
     * Recognize revenue for a specific schedule entry
     */
    void recognizeRevenue(RecognitionSchedule schedule);

    /**
     * Recognize all due revenue up to date
     */
    int recognizeAllDue(LocalDate asOfDate);

    /**
     * Calculate monthly recognition amount
     * For 12-month recognition: totalAmount / 12
     */
    BigDecimal calculateMonthlyAmount(CommissionPlan plan);

    /**
     * Get total amount recognized for a plan
     */
    BigDecimal getTotalRecognized(Long commissionPlanId);

    /**
     * Get total amount pending recognition for a plan
     */
    BigDecimal getTotalPending(Long commissionPlanId);
}
