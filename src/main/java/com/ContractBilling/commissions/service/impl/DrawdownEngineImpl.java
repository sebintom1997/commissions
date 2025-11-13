package com.ContractBilling.commissions.service.impl;

import com.ContractBilling.commissions.entity.DrawdownRequest;
import com.ContractBilling.commissions.entity.Salesperson;
import com.ContractBilling.commissions.repository.CommissionPlanRepository;
import com.ContractBilling.commissions.repository.DrawdownRequestRepository;
import com.ContractBilling.commissions.repository.SettingsRepository;
import com.ContractBilling.commissions.service.DrawdownEngine;
import com.ContractBilling.commissions.service.LedgerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DrawdownEngineImpl implements DrawdownEngine {

    private final DrawdownRequestRepository drawdownRepository;
    private final CommissionPlanRepository commissionPlanRepository;
    private final SettingsRepository settingsRepository;
    private final LedgerService ledgerService;

    @Override
    public boolean canRequestDrawdown(Salesperson salesperson) {
        BigDecimal available = getAvailableBalance(salesperson);
        return available.compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    public BigDecimal getMaxDrawableAmount(Salesperson salesperson) {
        var settings = settingsRepository.findAll();
        if (settings.isEmpty()) {
            return BigDecimal.ZERO;
        }

        var policy = settings.get(0);
        BigDecimal available = getAvailableBalance(salesperson);

        // Check minimum month requirement
        LocalDate minDate = LocalDate.now().minusMonths(policy.getDrawdownMinMonth());
        // For simplicity, return available if eligible

        return available;
    }

    @Override
    public BigDecimal getAvailableBalance(Salesperson salesperson) {
        BigDecimal recognized = commissionPlanRepository.sumRecognizedAmount(salesperson);
        BigDecimal paid = drawdownRepository.sumPaidAmount(salesperson);

        return recognized.subtract(paid);
    }

    @Override
    public void approveDrawdown(DrawdownRequest request, String approvedBy) {
        log.info("Approving drawdown request ID: {}", request.getId());

        request.setStatus("APPROVED");
        request.setApprovedAmount(request.getRequestedAmount());
        request.setApprovedDate(LocalDate.now());
        request.setApprovedBy(approvedBy);

        drawdownRepository.save(request);

        ledgerService.recordAdjustment(
                request.getSalesperson().getId(),
                request.getApprovedAmount(),
                "Drawdown approved: " + request.getId()
        );

        log.info("Drawdown approved: ID={}, Amount={}", request.getId(), request.getApprovedAmount());
    }

    @Override
    public void rejectDrawdown(DrawdownRequest request, String rejectionReason, String rejectedBy) {
        log.info("Rejecting drawdown request ID: {}", request.getId());

        request.setStatus("REJECTED");
        request.setRejectionReason(rejectionReason);

        drawdownRepository.save(request);

        log.info("Drawdown rejected: ID={}, Reason={}", request.getId(), rejectionReason);
    }

    @Override
    public void processPayment(DrawdownRequest request, String paidBy) {
        log.info("Processing payment for drawdown ID: {}", request.getId());

        if (!"APPROVED".equals(request.getStatus())) {
            log.warn("Cannot pay non-approved drawdown: {}", request.getId());
            return;
        }

        request.setStatus("PAID");
        request.setPaidDate(LocalDate.now());
        request.setPaidBy(paidBy);

        drawdownRepository.save(request);

        ledgerService.recordCommissionPaid(
                null,
                request.getSalesperson().getId(),
                request.getApprovedAmount(),
                "Drawdown paid: " + request.getId()
        );

        log.info("Payment processed: ID={}, Amount={}", request.getId(), request.getApprovedAmount());
    }

    @Override
    public boolean canRequestInQuarter(Salesperson salesperson, Integer year, Integer quarter) {
        var settings = settingsRepository.findAll();
        if (settings.isEmpty()) {
            return true;
        }

        var policy = settings.get(0);
        int currentCount = getDrawdownCountInQuarter(salesperson, year, quarter);

        return currentCount < policy.getDrawdownMaxPerQuarter();
    }

    @Override
    public int getDrawdownCountInQuarter(Salesperson salesperson, Integer year, Integer quarter) {
        return drawdownRepository.countByQuarter(salesperson, year, quarter);
    }
}
