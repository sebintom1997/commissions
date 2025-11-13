package com.ContractBilling.commissions.service;

import com.ContractBilling.commissions.entity.Placement;
import com.ContractBilling.commissions.entity.PlacementType;
import com.ContractBilling.commissions.entity.PolicySettings;
import com.ContractBilling.commissions.service.impl.CommissionCalculationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for CommissionCalculationService
 * Tests all financial calculations against expected values
 */
class CommissionCalculationServiceTest {

    private CommissionCalculationService calculationService;
    private PolicySettings settings;

    @BeforeEach
    void setUp() {
        calculationService = new CommissionCalculationServiceImpl();

        // Set up policy settings with values from example
        settings = new PolicySettings();
        settings.setLeavePercentage(new BigDecimal("14.54"));
        settings.setPrsiPercentage(new BigDecimal("11.25"));
        settings.setPensionPercentage(new BigDecimal("1.5"));
        settings.setAdminPercentage(new BigDecimal("6.00"));
        settings.setInsurancePercentage(new BigDecimal("2.00"));
        settings.setWeeksPerYear(45);
        settings.setFirstContractCommission(new BigDecimal("15.00"));
        settings.setSecondContractCommission(new BigDecimal("10.00"));
        settings.setThirdContractCommission(new BigDecimal("8.00"));
    }

    @Test
    void testCalculateHourlyPayCost() {
        // Test data from example
        BigDecimal annualSalary = new BigDecimal("55000");
        BigDecimal hoursPerWeek = new BigDecimal("39");

        // Calculated: 55000 / 52 / 39 × 1.1454 × 1.1125 × 1.015 = €35.08/hour
        BigDecimal result = calculationService.calculateHourlyPayCost(
                annualSalary, hoursPerWeek, settings);

        assertThat(result).isEqualByComparingTo(new BigDecimal("35.08"));
    }

    @Test
    void testCalculateMarginPerHour() {
        BigDecimal billRate = new BigDecimal("40.28");
        BigDecimal hourlyPayCost = new BigDecimal("35.08");

        // Calculated: 40.28 - 35.08 = €5.20/hour
        BigDecimal result = calculationService.calculateMarginPerHour(billRate, hourlyPayCost);

        assertThat(result).isEqualByComparingTo(new BigDecimal("5.20"));
    }

    @Test
    void testCalculateWeeklyMargin() {
        BigDecimal marginPerHour = new BigDecimal("5.20");
        BigDecimal hoursPerWeek = new BigDecimal("39");

        // Calculated: €202.80/week (5.20 × 39)
        BigDecimal result = calculationService.calculateWeeklyMargin(marginPerHour, hoursPerWeek);

        assertThat(result).isEqualByComparingTo(new BigDecimal("202.80"));
    }

    @Test
    void testCalculateGrossAnnualMargin() {
        BigDecimal weeklyMargin = new BigDecimal("202.80");
        Integer weeksPerYear = 45;

        // Calculated: €9,126.00/year (202.80 × 45)
        BigDecimal result = calculationService.calculateGrossAnnualMargin(weeklyMargin, weeksPerYear);

        assertThat(result).isEqualByComparingTo(new BigDecimal("9126.00"));
    }

    @Test
    void testCalculateNetAnnualMargin() {
        BigDecimal grossMargin = new BigDecimal("9126.00");
        BigDecimal adminPercentage = new BigDecimal("6.00");
        BigDecimal insurancePercentage = new BigDecimal("2.00");
        BigDecimal fixedCosts = BigDecimal.ZERO;

        // Overheads: (6% + 2%) × 9126.00 = 730.08
        // Net: 9126.00 - 730.08 = 8395.92
        BigDecimal result = calculationService.calculateNetAnnualMargin(
                grossMargin, adminPercentage, insurancePercentage, fixedCosts);

        assertThat(result).isEqualByComparingTo(new BigDecimal("8395.92"));
    }

    @Test
    void testDetermineCommissionPercentage_FirstContract() {
        BigDecimal result = calculationService.determineCommissionPercentage(1, settings);
        assertThat(result).isEqualByComparingTo(new BigDecimal("15.00"));
    }

    @Test
    void testDetermineCommissionPercentage_SecondContract() {
        BigDecimal result = calculationService.determineCommissionPercentage(2, settings);
        assertThat(result).isEqualByComparingTo(new BigDecimal("10.00"));
    }

    @Test
    void testDetermineCommissionPercentage_ThirdContract() {
        BigDecimal result = calculationService.determineCommissionPercentage(3, settings);
        assertThat(result).isEqualByComparingTo(new BigDecimal("8.00"));
    }

    @Test
    void testDetermineCommissionPercentage_FourthContractAndBeyond() {
        BigDecimal result = calculationService.determineCommissionPercentage(4, settings);
        assertThat(result).isEqualByComparingTo(new BigDecimal("8.00"));

        result = calculationService.determineCommissionPercentage(10, settings);
        assertThat(result).isEqualByComparingTo(new BigDecimal("8.00"));
    }

    @Test
    void testCalculateCommissionTotal() {
        BigDecimal netMargin = new BigDecimal("8395.92");
        BigDecimal commissionPercentage = new BigDecimal("15.00");

        // Calculated: 8395.92 × 0.15 = 1259.39
        BigDecimal result = calculationService.calculateCommissionTotal(
                netMargin, commissionPercentage);

        assertThat(result).isEqualByComparingTo(new BigDecimal("1259.39"));
    }

    @Test
    void testCalculateContractorCommission_FullExample() {
        // Full end-to-end test with example data
        Placement placement = new Placement();
        placement.setPlacementType(PlacementType.CONTRACTOR);
        placement.setAnnualSalary(new BigDecimal("55000"));
        placement.setHoursPerWeek(new BigDecimal("39"));
        placement.setWeeksPerYear(45);
        placement.setBillRate(new BigDecimal("40.28"));
        placement.setAdminPercentage(new BigDecimal("6.00"));
        placement.setInsurancePercentage(new BigDecimal("2.00"));
        placement.setFixedCosts(BigDecimal.ZERO);
        placement.setSequenceNumber(1); // First contract

        calculationService.calculateContractorCommission(placement, settings);

        // Verify all calculated fields
        assertThat(placement.getHourlyPayCost()).isEqualByComparingTo(new BigDecimal("35.08"));
        assertThat(placement.getMarginPerHour()).isEqualByComparingTo(new BigDecimal("5.20"));
        assertThat(placement.getWeeklyMargin()).isEqualByComparingTo(new BigDecimal("202.80"));
        assertThat(placement.getGrossAnnualMargin()).isEqualByComparingTo(new BigDecimal("9126.00"));
        assertThat(placement.getNetAnnualMargin()).isEqualByComparingTo(new BigDecimal("8395.92"));
        assertThat(placement.getCommissionPercentage()).isEqualByComparingTo(new BigDecimal("15.00"));
        assertThat(placement.getCommissionTotal()).isEqualByComparingTo(new BigDecimal("1259.39"));
    }

    @Test
    void testCalculateContractorCommission_SecondContract() {
        Placement placement = new Placement();
        placement.setPlacementType(PlacementType.CONTRACTOR);
        placement.setAnnualSalary(new BigDecimal("55000"));
        placement.setHoursPerWeek(new BigDecimal("39"));
        placement.setWeeksPerYear(45);
        placement.setBillRate(new BigDecimal("40.28"));
        placement.setAdminPercentage(new BigDecimal("6.00"));
        placement.setInsurancePercentage(new BigDecimal("2.00"));
        placement.setFixedCosts(BigDecimal.ZERO);
        placement.setSequenceNumber(2); // Second contract

        calculationService.calculateContractorCommission(placement, settings);

        // Commission should be 10% for second contract
        assertThat(placement.getCommissionPercentage()).isEqualByComparingTo(new BigDecimal("10.00"));
        // 8395.92 × 0.10 = 839.59
        assertThat(placement.getCommissionTotal()).isEqualByComparingTo(new BigDecimal("839.59"));
    }

    @Test
    void testCalculateContractorCommission_ThirdContract() {
        Placement placement = new Placement();
        placement.setPlacementType(PlacementType.CONTRACTOR);
        placement.setAnnualSalary(new BigDecimal("55000"));
        placement.setHoursPerWeek(new BigDecimal("39"));
        placement.setWeeksPerYear(45);
        placement.setBillRate(new BigDecimal("40.28"));
        placement.setAdminPercentage(new BigDecimal("6.00"));
        placement.setInsurancePercentage(new BigDecimal("2.00"));
        placement.setFixedCosts(BigDecimal.ZERO);
        placement.setSequenceNumber(3); // Third contract

        calculationService.calculateContractorCommission(placement, settings);

        // Commission should be 8% for third contract and beyond
        assertThat(placement.getCommissionPercentage()).isEqualByComparingTo(new BigDecimal("8.00"));
        // 8395.92 × 0.08 = 671.67
        assertThat(placement.getCommissionTotal()).isEqualByComparingTo(new BigDecimal("671.67"));
    }

    @Test
    void testCalculatePermanentCommission() {
        Placement placement = new Placement();
        placement.setPlacementType(PlacementType.PERMANENT);
        placement.setPlacementFee(new BigDecimal("10000"));
        placement.setSequenceNumber(1); // First contract

        calculationService.calculatePermanentCommission(placement, settings);

        // For permanent placements, net margin = placement fee
        assertThat(placement.getNetAnnualMargin()).isEqualByComparingTo(new BigDecimal("10000"));
        assertThat(placement.getCommissionPercentage()).isEqualByComparingTo(new BigDecimal("15.00"));
        // 10000 × 0.15 = 1500
        assertThat(placement.getCommissionTotal()).isEqualByComparingTo(new BigDecimal("1500.00"));
    }

    @Test
    void testCalculateContractorCommission_WithFixedCosts() {
        Placement placement = new Placement();
        placement.setPlacementType(PlacementType.CONTRACTOR);
        placement.setAnnualSalary(new BigDecimal("55000"));
        placement.setHoursPerWeek(new BigDecimal("39"));
        placement.setWeeksPerYear(45);
        placement.setBillRate(new BigDecimal("40.28"));
        placement.setAdminPercentage(new BigDecimal("6.00"));
        placement.setInsurancePercentage(new BigDecimal("2.00"));
        placement.setFixedCosts(new BigDecimal("500")); // €500 fixed costs
        placement.setSequenceNumber(1);

        calculationService.calculateContractorCommission(placement, settings);

        // Gross: 9126.00
        // Overheads: 730.08 + 500 = 1230.08
        // Net: 9126.00 - 1230.08 = 7895.92
        assertThat(placement.getNetAnnualMargin()).isEqualByComparingTo(new BigDecimal("7895.92"));
        // Commission: 7895.92 × 0.15 = 1184.39
        assertThat(placement.getCommissionTotal()).isEqualByComparingTo(new BigDecimal("1184.39"));
    }

    @Test
    void testCalculateContractorCommission_With52Weeks() {
        Placement placement = new Placement();
        placement.setPlacementType(PlacementType.CONTRACTOR);
        placement.setAnnualSalary(new BigDecimal("55000"));
        placement.setHoursPerWeek(new BigDecimal("39"));
        placement.setWeeksPerYear(52); // Full year
        placement.setBillRate(new BigDecimal("40.28"));
        placement.setAdminPercentage(new BigDecimal("6.00"));
        placement.setInsurancePercentage(new BigDecimal("2.00"));
        placement.setFixedCosts(BigDecimal.ZERO);
        placement.setSequenceNumber(1);

        calculationService.calculateContractorCommission(placement, settings);

        // Weekly margin: 202.80
        // Gross: 202.80 × 52 = 10,545.60
        assertThat(placement.getGrossAnnualMargin()).isEqualByComparingTo(new BigDecimal("10545.60"));
        // Overheads: 843.65
        // Net: 9701.95
        assertThat(placement.getNetAnnualMargin()).isEqualByComparingTo(new BigDecimal("9701.95"));
        // Commission: 1455.29
        assertThat(placement.getCommissionTotal()).isEqualByComparingTo(new BigDecimal("1455.29"));
    }
}
