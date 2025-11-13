package com.ContractBilling.commissions.service.impl;

import com.ContractBilling.commissions.entity.Salesperson;
import com.ContractBilling.commissions.repository.CommissionPlanRepository;
import com.ContractBilling.commissions.repository.DrawdownRequestRepository;
import com.ContractBilling.commissions.repository.LedgerRepository;
import com.ContractBilling.commissions.repository.SalespersonRepository;
import com.ContractBilling.commissions.service.DrawdownEngine;
import com.ContractBilling.commissions.service.ReportingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ReportingServiceImpl implements ReportingService {

    private final SalespersonRepository salespersonRepository;
    private final CommissionPlanRepository commissionPlanRepository;
    private final DrawdownRequestRepository drawdownRepository;
    private final LedgerRepository ledgerRepository;
    private final DrawdownEngine drawdownEngine;

    @Override
    public Map<String, Object> getSalespersonDashboard(Long salespersonId) {
        Salesperson salesperson = salespersonRepository.findById(salespersonId).orElseThrow();

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("salespersonId", salespersonId);
        dashboard.put("salespersonName", salesperson.getName());

        // Financial summary
        BigDecimal totalPlanned = commissionPlanRepository.sumPlannedAmount(salesperson);
        BigDecimal totalRecognized = commissionPlanRepository.sumRecognizedAmount(salesperson);
        BigDecimal totalPaid = drawdownRepository.sumPaidAmount(salesperson);
        BigDecimal outstanding = totalRecognized.subtract(totalPaid);

        dashboard.put("financialSummary", Map.of(
                "totalPlanned", totalPlanned,
                "totalRecognized", totalRecognized,
                "totalPaid", totalPaid,
                "outstanding", outstanding
        ));

        // Availability
        BigDecimal available = drawdownEngine.getAvailableBalance(salesperson);
        dashboard.put("availableForDrawdown", available);

        // Commission plans count
        int pendingPlans = (int) commissionPlanRepository.findByStatus(com.ContractBilling.commissions.entity.CommissionPlanStatus.PLANNED)
                .stream()
                .filter(p -> p.getSalesperson().getId().equals(salespersonId))
                .count();

        dashboard.put("pendingPlans", pendingPlans);
        dashboard.put("generatedAt", LocalDateTime.now());

        return dashboard;
    }

    @Override
    public Map<String, Object> getPeriodSummary(Long salespersonId, LocalDate startDate, LocalDate endDate) {
        Salesperson salesperson = salespersonRepository.findById(salespersonId).orElseThrow();

        Map<String, Object> summary = new HashMap<>();
        summary.put("period", Map.of("start", startDate, "end", endDate));

        var ledgerEntries = ledgerRepository.findByDateRange(salesperson, startDate.atStartOfDay(), endDate.atStartOfDay().plusDays(1));

        BigDecimal totalAccrued = BigDecimal.ZERO;
        BigDecimal totalRecognized = BigDecimal.ZERO;
        BigDecimal totalPaid = BigDecimal.ZERO;

        for (var entry : ledgerEntries) {
            if ("COMMISSION_ACCRUED".equals(entry.getEntryType().toString())) {
                totalAccrued = totalAccrued.add(entry.getAmount());
            } else if ("COMMISSION_RECOGNIZED".equals(entry.getEntryType().toString())) {
                totalRecognized = totalRecognized.add(entry.getAmount());
            } else if ("COMMISSION_PAID".equals(entry.getEntryType().toString())) {
                totalPaid = totalPaid.add(entry.getAmount());
            }
        }

        summary.put("accrued", totalAccrued);
        summary.put("recognized", totalRecognized);
        summary.put("paid", totalPaid);
        summary.put("transactionCount", ledgerEntries.size());

        return summary;
    }

    @Override
    public Map<String, Object> getCommissionByPlacement(Long salespersonId) {
        Salesperson salesperson = salespersonRepository.findById(salespersonId).orElseThrow();
        Map<String, Object> report = new HashMap<>();

        var plans = commissionPlanRepository.findBySalesperson(salesperson);
        report.put("placementCount", plans.size());
        report.put("placements", plans);

        return report;
    }

    @Override
    public Map<String, Object> getRecognitionStatus(Long salespersonId) {
        Salesperson salesperson = salespersonRepository.findById(salespersonId).orElseThrow();

        Map<String, Object> status = new HashMap<>();
        var plans = commissionPlanRepository.findBySalesperson(salesperson);

        long totalPlans = plans.size();
        long recognizedPlans = plans.stream()
                .filter(p -> p.getStatus() == com.ContractBilling.commissions.entity.CommissionPlanStatus.RECOGNIZED)
                .count();
        long paidPlans = plans.stream()
                .filter(p -> p.getStatus() == com.ContractBilling.commissions.entity.CommissionPlanStatus.PAID)
                .count();

        status.put("totalPlans", totalPlans);
        status.put("recognized", recognizedPlans);
        status.put("paid", paidPlans);
        status.put("pending", totalPlans - recognizedPlans);
        status.put("recognitionPercentage", totalPlans > 0 ? (recognizedPlans * 100 / totalPlans) : 0);

        return status;
    }

    @Override
    public Map<String, Object> getDrawdownHistory(Long salespersonId) {
        Salesperson salesperson = salespersonRepository.findById(salespersonId).orElseThrow();

        Map<String, Object> history = new HashMap<>();
        var drawdowns = drawdownRepository.findBySalesperson(salesperson);

        history.put("totalRequests", drawdowns.size());
        history.put("approved", drawdowns.stream().filter(d -> "APPROVED".equals(d.getStatus())).count());
        history.put("paid", drawdowns.stream().filter(d -> "PAID".equals(d.getStatus())).count());
        history.put("rejected", drawdowns.stream().filter(d -> "REJECTED".equals(d.getStatus())).count());
        history.put("requests", drawdowns);

        return history;
    }

    @Override
    public Map<String, Object> getTopPerformers(int limit) {
        var salespeople = salespersonRepository.findAll();

        Map<String, Object> report = new HashMap<>();
        var topPerformers = salespeople.stream()
                .map(s -> Map.of(
                        "salespersonId", (Object) s.getId(),
                        "name", s.getName(),
                        "totalCommission", commissionPlanRepository.sumPlannedAmount(s)
                ))
                .sorted((a, b) -> ((BigDecimal) b.get("totalCommission")).compareTo((BigDecimal) a.get("totalCommission")))
                .limit(limit)
                .toList();

        report.put("topPerformers", topPerformers);
        return report;
    }

    @Override
    public Map<String, Object> getSystemHealth() {
        Map<String, Object> health = new HashMap<>();

        long totalSalespeople = salespersonRepository.count();
        long totalPlans = commissionPlanRepository.count();
        long totalDrawdowns = drawdownRepository.count();

        health.put("totalSalespeople", totalSalespeople);
        health.put("totalPlans", totalPlans);
        health.put("totalDrawdowns", totalDrawdowns);
        health.put("timestamp", LocalDateTime.now());

        return health;
    }
}
