package com.ContractBilling.commissions;

import com.ContractBilling.commissions.entity.Salesperson;
import com.ContractBilling.commissions.repository.SalespersonRepository;
import com.ContractBilling.commissions.service.ReportingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ReportingServiceIntegrationTest {

    @Autowired
    private ReportingService reportingService;

    @Autowired
    private SalespersonRepository salespersonRepository;

    private Salesperson salesperson;

    @BeforeEach
    public void setUp() {
        salesperson = new Salesperson();
        salesperson.setName("Reporting Test User");
        salesperson.setEmail("report@example.com");
        salespersonRepository.save(salesperson);
    }

    @Test
    public void testGetSalespersonDashboard() {
        Map<String, Object> dashboard = reportingService.getSalespersonDashboard(salesperson.getId());

        assertNotNull(dashboard);
        assertTrue(dashboard.containsKey("salespersonId"));
        assertTrue(dashboard.containsKey("salespersonName"));
        assertTrue(dashboard.containsKey("financialSummary"));
        assertTrue(dashboard.containsKey("availableForDrawdown"));
        assertTrue(dashboard.containsKey("pendingPlans"));
    }

    @Test
    public void testGetPeriodSummary() {
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();

        Map<String, Object> summary = reportingService.getPeriodSummary(salesperson.getId(), startDate, endDate);

        assertNotNull(summary);
        assertTrue(summary.containsKey("period"));
        assertTrue(summary.containsKey("accrued"));
        assertTrue(summary.containsKey("recognized"));
        assertTrue(summary.containsKey("paid"));
        assertTrue(summary.containsKey("transactionCount"));
    }

    @Test
    public void testGetSystemHealth() {
        Map<String, Object> health = reportingService.getSystemHealth();

        assertNotNull(health);
        assertTrue(health.containsKey("totalSalespeople"));
        assertTrue(health.containsKey("totalPlans"));
        assertTrue(health.containsKey("totalDrawdowns"));
        assertTrue(health.containsKey("timestamp"));
    }

    @Test
    public void testGetTopPerformers() {
        Map<String, Object> report = reportingService.getTopPerformers(10);

        assertNotNull(report);
        assertTrue(report.containsKey("topPerformers"));
    }
}
