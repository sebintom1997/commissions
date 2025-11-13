package com.ContractBilling.commissions.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public interface ReportingService {

    // Salesperson dashboard
    Map<String, Object> getSalespersonDashboard(Long salespersonId);

    // Period summary (month, quarter, year)
    Map<String, Object> getPeriodSummary(Long salespersonId, LocalDate startDate, LocalDate endDate);

    // Commission breakdown by placement
    Map<String, Object> getCommissionByPlacement(Long salespersonId);

    // Revenue recognition status
    Map<String, Object> getRecognitionStatus(Long salespersonId);

    // Drawdown history
    Map<String, Object> getDrawdownHistory(Long salespersonId);

    // Top performers report
    Map<String, Object> getTopPerformers(int limit);

    // Overall system health
    Map<String, Object> getSystemHealth();
}
