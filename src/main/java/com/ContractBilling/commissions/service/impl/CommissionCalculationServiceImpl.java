package com.ContractBilling.commissions.service.impl;

import com.ContractBilling.commissions.entity.Placement;
import com.ContractBilling.commissions.entity.PolicySettings;
import com.ContractBilling.commissions.service.CommissionCalculationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Implementation of CommissionCalculationService
 * Handles all financial calculations for placements
 */
@Service
@Slf4j
public class CommissionCalculationServiceImpl implements CommissionCalculationService {

    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    private static final BigDecimal WEEKS_PER_YEAR = new BigDecimal("52");
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    @Override
    public BigDecimal calculateHourlyPayCost(
            BigDecimal annualSalary,
            BigDecimal hoursPerWeek,
            PolicySettings settings) {

        log.debug("Calculating hourly pay cost for salary: {}, hours/week: {}", annualSalary, hoursPerWeek);

        // Base hourly rate = Annual Salary ÷ 52 ÷ Hours/Week
        BigDecimal baseHourlyRate = annualSalary
                .divide(WEEKS_PER_YEAR, 10, ROUNDING_MODE)
                .divide(hoursPerWeek, 10, ROUNDING_MODE);

        // Apply load factors: (1 + Leave%) × (1 + PRSI%) × (1 + Pension%)
        BigDecimal leaveMultiplier = BigDecimal.ONE.add(
                settings.getLeavePercentage().divide(ONE_HUNDRED, 10, ROUNDING_MODE)
        );
        BigDecimal prsiMultiplier = BigDecimal.ONE.add(
                settings.getPrsiPercentage().divide(ONE_HUNDRED, 10, ROUNDING_MODE)
        );
        BigDecimal pensionMultiplier = BigDecimal.ONE.add(
                settings.getPensionPercentage().divide(ONE_HUNDRED, 10, ROUNDING_MODE)
        );

        BigDecimal hourlyPayCost = baseHourlyRate
                .multiply(leaveMultiplier)
                .multiply(prsiMultiplier)
                .multiply(pensionMultiplier)
                .setScale(SCALE, ROUNDING_MODE);

        log.debug("Calculated hourly pay cost: {}", hourlyPayCost);
        return hourlyPayCost;
    }

    @Override
    public BigDecimal calculateMarginPerHour(
            BigDecimal billRate,
            BigDecimal hourlyPayCost) {

        log.debug("Calculating margin per hour: billRate={}, payRate={}", billRate, hourlyPayCost);

        BigDecimal marginPerHour = billRate
                .subtract(hourlyPayCost)
                .setScale(SCALE, ROUNDING_MODE);

        log.debug("Calculated margin per hour: {}", marginPerHour);
        return marginPerHour;
    }

    @Override
    public BigDecimal calculateWeeklyMargin(
            BigDecimal marginPerHour,
            BigDecimal hoursPerWeek) {

        log.debug("Calculating weekly margin: marginPerHour={}, hoursPerWeek={}", marginPerHour, hoursPerWeek);

        BigDecimal weeklyMargin = marginPerHour
                .multiply(hoursPerWeek)
                .setScale(SCALE, ROUNDING_MODE);

        log.debug("Calculated weekly margin: {}", weeklyMargin);
        return weeklyMargin;
    }

    @Override
    public BigDecimal calculateGrossAnnualMargin(
            BigDecimal weeklyMargin,
            Integer weeksPerYear) {

        log.debug("Calculating gross annual margin: weeklyMargin={}, weeksPerYear={}", weeklyMargin, weeksPerYear);

        BigDecimal grossAnnualMargin = weeklyMargin
                .multiply(new BigDecimal(weeksPerYear))
                .setScale(SCALE, ROUNDING_MODE);

        log.debug("Calculated gross annual margin: {}", grossAnnualMargin);
        return grossAnnualMargin;
    }

    @Override
    public BigDecimal calculateNetAnnualMargin(
            BigDecimal grossMargin,
            BigDecimal adminPercentage,
            BigDecimal insurancePercentage,
            BigDecimal fixedCosts) {

        log.debug("Calculating net annual margin: gross={}, admin%={}, insurance%={}, fixed={}",
                grossMargin, adminPercentage, insurancePercentage, fixedCosts);

        // Calculate overhead costs
        BigDecimal adminCost = grossMargin
                .multiply(adminPercentage.divide(ONE_HUNDRED, 10, ROUNDING_MODE))
                .setScale(SCALE, ROUNDING_MODE);

        BigDecimal insuranceCost = grossMargin
                .multiply(insurancePercentage.divide(ONE_HUNDRED, 10, ROUNDING_MODE))
                .setScale(SCALE, ROUNDING_MODE);

        BigDecimal totalOverheads = adminCost
                .add(insuranceCost)
                .add(fixedCosts != null ? fixedCosts : BigDecimal.ZERO)
                .setScale(SCALE, ROUNDING_MODE);

        BigDecimal netAnnualMargin = grossMargin
                .subtract(totalOverheads)
                .setScale(SCALE, ROUNDING_MODE);

        log.debug("Calculated overheads: admin={}, insurance={}, fixed={}, total={}",
                adminCost, insuranceCost, fixedCosts, totalOverheads);
        log.debug("Calculated net annual margin: {}", netAnnualMargin);

        return netAnnualMargin;
    }

    @Override
    public BigDecimal determineCommissionPercentage(
            Integer sequenceNumber,
            PolicySettings settings) {

        log.debug("Determining commission percentage for sequence number: {}", sequenceNumber);

        BigDecimal commissionPercentage;

        if (sequenceNumber == 1) {
            commissionPercentage = settings.getFirstContractCommission();
        } else if (sequenceNumber == 2) {
            commissionPercentage = settings.getSecondContractCommission();
        } else {
            // 3rd contract and beyond
            commissionPercentage = settings.getThirdContractCommission();
        }

        log.debug("Commission percentage for sequence {}: {}%", sequenceNumber, commissionPercentage);
        return commissionPercentage;
    }

    @Override
    public BigDecimal calculateCommissionTotal(
            BigDecimal netMargin,
            BigDecimal commissionPercentage) {

        log.debug("Calculating commission total: netMargin={}, percentage={}%", netMargin, commissionPercentage);

        BigDecimal commissionTotal = netMargin
                .multiply(commissionPercentage.divide(ONE_HUNDRED, 10, ROUNDING_MODE))
                .setScale(SCALE, ROUNDING_MODE);

        log.debug("Calculated commission total: {}", commissionTotal);
        return commissionTotal;
    }

    @Override
    public void calculateContractorCommission(Placement placement, PolicySettings settings) {
        log.info("Calculating contractor commission for placement ID: {}", placement.getId());

        // Step 1: Calculate hourly pay cost
        BigDecimal hourlyPayCost = calculateHourlyPayCost(
                placement.getAnnualSalary(),
                placement.getHoursPerWeek(),
                settings
        );
        placement.setHourlyPayCost(hourlyPayCost);

        // Step 2: Calculate margin per hour
        BigDecimal marginPerHour = calculateMarginPerHour(
                placement.getBillRate(),
                hourlyPayCost
        );
        placement.setMarginPerHour(marginPerHour);

        // Step 3: Calculate weekly margin
        BigDecimal weeklyMargin = calculateWeeklyMargin(
                marginPerHour,
                placement.getHoursPerWeek()
        );
        placement.setWeeklyMargin(weeklyMargin);

        // Step 4: Calculate gross annual margin
        // Use weeks per year from placement, or default from settings
        Integer weeksPerYear = placement.getWeeksPerYear() != null
                ? placement.getWeeksPerYear()
                : settings.getWeeksPerYear();

        BigDecimal grossAnnualMargin = calculateGrossAnnualMargin(
                weeklyMargin,
                weeksPerYear
        );
        placement.setGrossAnnualMargin(grossAnnualMargin);

        // Step 5: Calculate net annual margin (after overheads)
        BigDecimal netAnnualMargin = calculateNetAnnualMargin(
                grossAnnualMargin,
                placement.getAdminPercentage(),
                placement.getInsurancePercentage(),
                placement.getFixedCosts() != null ? placement.getFixedCosts() : BigDecimal.ZERO
        );
        placement.setNetAnnualMargin(netAnnualMargin);

        // Step 6: Determine commission percentage based on sequence
        BigDecimal commissionPercentage = determineCommissionPercentage(
                placement.getSequenceNumber(),
                settings
        );
        placement.setCommissionPercentage(commissionPercentage);

        // Step 7: Calculate total commission
        BigDecimal commissionTotal = calculateCommissionTotal(
                netAnnualMargin,
                commissionPercentage
        );
        placement.setCommissionTotal(commissionTotal);

        log.info("Commission calculation complete: hourlyPayCost={}, marginPerHour={}, " +
                        "weeklyMargin={}, grossMargin={}, netMargin={}, commissionPct={}%, commissionTotal={}",
                hourlyPayCost, marginPerHour, weeklyMargin, grossAnnualMargin,
                netAnnualMargin, commissionPercentage, commissionTotal);
    }

    @Override
    public void calculatePermanentCommission(Placement placement, PolicySettings settings) {
        log.info("Calculating permanent placement commission for placement ID: {}", placement.getId());

        // For permanent placements, commission is based on placement fee
        BigDecimal placementFee = placement.getPlacementFee();

        // Calculate net margin (placement fee minus overheads if applicable)
        // For permanent placements, we use the placement fee directly as the margin
        BigDecimal netMargin = placementFee;

        placement.setNetAnnualMargin(netMargin);

        // Determine commission percentage based on sequence
        BigDecimal commissionPercentage = determineCommissionPercentage(
                placement.getSequenceNumber(),
                settings
        );
        placement.setCommissionPercentage(commissionPercentage);

        // Calculate total commission
        BigDecimal commissionTotal = calculateCommissionTotal(
                netMargin,
                commissionPercentage
        );
        placement.setCommissionTotal(commissionTotal);

        log.info("Permanent commission calculation complete: netMargin={}, commissionPct={}%, commissionTotal={}",
                netMargin, commissionPercentage, commissionTotal);
    }
}
