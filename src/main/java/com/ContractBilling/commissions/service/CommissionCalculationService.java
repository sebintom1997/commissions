package com.ContractBilling.commissions.service;

import com.ContractBilling.commissions.entity.Placement;
import com.ContractBilling.commissions.entity.PolicySettings;

import java.math.BigDecimal;

/**
 * Service interface for commission calculation operations
 * Handles all financial calculations for contractor and permanent placements
 */
public interface CommissionCalculationService {

    /**
     * Calculate hourly pay cost including all load factors (leave, PRSI, pension)
     *
     * Formula: (Annual Salary ÷ 52 ÷ Hours/Week) × (1 + Leave%) × (1 + PRSI%) × (1 + Pension%)
     *
     * Example:
     * - Annual Salary: €55,000
     * - Hours/Week: 39
     * - Leave: 14.54%, PRSI: 11.25%, Pension: 1.5%
     * - Result: €35.02/hour
     *
     * @param annualSalary The annual salary of the contractor
     * @param hoursPerWeek The number of hours worked per week
     * @param settings Policy settings containing leave, PRSI, and pension percentages
     * @return The hourly pay cost including all load factors
     */
    BigDecimal calculateHourlyPayCost(
            BigDecimal annualSalary,
            BigDecimal hoursPerWeek,
            PolicySettings settings
    );

    /**
     * Calculate margin per hour (profit per hour)
     *
     * Formula: Bill Rate - Hourly Pay Cost
     *
     * Example:
     * - Bill Rate: €40.28
     * - Hourly Pay Cost: €35.02
     * - Result: €5.26/hour
     *
     * @param billRate The hourly rate billed to the client
     * @param hourlyPayCost The hourly cost of paying the contractor
     * @return The margin (profit) per hour
     */
    BigDecimal calculateMarginPerHour(
            BigDecimal billRate,
            BigDecimal hourlyPayCost
    );

    /**
     * Calculate weekly margin (profit per week)
     *
     * Formula: Margin/Hour × Hours/Week
     *
     * Example:
     * - Margin/Hour: €5.26
     * - Hours/Week: 39
     * - Result: €205.14/week
     *
     * @param marginPerHour The profit per hour
     * @param hoursPerWeek The number of hours worked per week
     * @return The weekly margin (profit)
     */
    BigDecimal calculateWeeklyMargin(
            BigDecimal marginPerHour,
            BigDecimal hoursPerWeek
    );

    /**
     * Calculate gross annual margin (profit per year before overheads)
     *
     * Formula: Weekly Margin × Weeks/Year
     *
     * Example:
     * - Weekly Margin: €204.75
     * - Weeks/Year: 45
     * - Result: €9,213.75/year
     *
     * @param weeklyMargin The weekly margin (profit)
     * @param weeksPerYear The number of billable weeks per year
     * @return The gross annual margin before overheads
     */
    BigDecimal calculateGrossAnnualMargin(
            BigDecimal weeklyMargin,
            Integer weeksPerYear
    );

    /**
     * Calculate net annual margin (profit after overheads)
     *
     * Formula: Gross Margin - [(Admin% × Gross) + (Insurance% × Gross) + Fixed Costs]
     *
     * Example:
     * - Gross Margin: €9,219.92
     * - Admin: 6%, Insurance: 2%, Fixed: €0
     * - Overheads: €737.59
     * - Result: €8,482.33/year
     *
     * @param grossMargin The gross annual margin before overheads
     * @param adminPercentage The administrative overhead percentage
     * @param insurancePercentage The insurance overhead percentage
     * @param fixedCosts Any fixed costs to deduct
     * @return The net annual margin after all overheads
     */
    BigDecimal calculateNetAnnualMargin(
            BigDecimal grossMargin,
            BigDecimal adminPercentage,
            BigDecimal insurancePercentage,
            BigDecimal fixedCosts
    );

    /**
     * Determine commission percentage based on sequence number
     *
     * Rules:
     * - 1st contract (sequence 1): 15%
     * - 2nd contract (sequence 2): 10%
     * - 3rd+ contract (sequence 3+): 8%
     *
     * @param sequenceNumber The sequence number (1st, 2nd, 3rd+ contract for this contractor at this client)
     * @param settings Policy settings containing commission tier percentages
     * @return The commission percentage to apply
     */
    BigDecimal determineCommissionPercentage(
            Integer sequenceNumber,
            PolicySettings settings
    );

    /**
     * Calculate total commission amount
     *
     * Formula: Net Margin × Commission Percentage
     *
     * Example:
     * - Net Margin: €8,482.33
     * - Commission %: 15%
     * - Result: €1,272.35
     *
     * @param netMargin The net margin (commission base)
     * @param commissionPercentage The commission percentage to apply
     * @return The total commission amount
     */
    BigDecimal calculateCommissionTotal(
            BigDecimal netMargin,
            BigDecimal commissionPercentage
    );

    /**
     * Calculate all financial fields for a contractor placement
     * Sets: hourlyPayCost, marginPerHour, weeklyMargin, grossAnnualMargin,
     *       netAnnualMargin, commissionPercentage, commissionTotal
     *
     * @param placement The placement entity to calculate for
     * @param settings Policy settings containing all calculation parameters
     */
    void calculateContractorCommission(Placement placement, PolicySettings settings);

    /**
     * Calculate all financial fields for a permanent placement
     * Sets: commissionPercentage, commissionTotal
     *
     * @param placement The placement entity to calculate for
     * @param settings Policy settings containing commission percentages
     */
    void calculatePermanentCommission(Placement placement, PolicySettings settings);
}
