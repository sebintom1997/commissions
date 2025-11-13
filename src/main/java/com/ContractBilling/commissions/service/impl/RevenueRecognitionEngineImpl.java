package com.ContractBilling.commissions.service.impl;

import com.ContractBilling.commissions.entity.CommissionPlan;
import com.ContractBilling.commissions.entity.CommissionPlanStatus;
import com.ContractBilling.commissions.entity.RecognitionSchedule;
import com.ContractBilling.commissions.repository.CommissionPlanRepository;
import com.ContractBilling.commissions.repository.RecognitionScheduleRepository;
import com.ContractBilling.commissions.service.LedgerService;
import com.ContractBilling.commissions.service.RevenueRecognitionEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RevenueRecognitionEngineImpl implements RevenueRecognitionEngine {

    private final RecognitionScheduleRepository scheduleRepository;
    private final CommissionPlanRepository planRepository;
    private final LedgerService ledgerService;

    @Override
    public List<RecognitionSchedule> generateRecognitionSchedule(CommissionPlan plan) {
        log.info("Generating recognition schedule for commission plan ID: {}", plan.getId());

        List<RecognitionSchedule> schedules = new ArrayList<>();

        // Get recognition parameters
        Integer monthsToRecognize = plan.getMonthsToRecognize() != null ? plan.getMonthsToRecognize() : 12;
        LocalDate startDate = plan.getRecognitionStartDate() != null
            ? plan.getRecognitionStartDate()
            : LocalDate.now();

        BigDecimal monthlyAmount = calculateMonthlyAmount(plan);

        // Generate monthly entries
        for (int month = 1; month <= monthsToRecognize; month++) {
            LocalDate recognitionDate = startDate.plusMonths(month - 1);

            RecognitionSchedule schedule = RecognitionSchedule.builder()
                    .commissionPlan(plan)
                    .month(month)
                    .recognitionDate(recognitionDate)
                    .plannedAmount(monthlyAmount)
                    .recognizedAmount(BigDecimal.ZERO)
                    .status("PENDING")
                    .build();

            scheduleRepository.save(schedule);
            schedules.add(schedule);
        }

        log.info("Generated {} recognition schedules for plan ID: {}", monthsToRecognize, plan.getId());
        return schedules;
    }

    @Override
    public void recognizeRevenue(RecognitionSchedule schedule) {
        log.info("Recognizing revenue for schedule ID: {}", schedule.getId());

        if ("RECOGNIZED".equals(schedule.getStatus()) || "PAID".equals(schedule.getStatus())) {
            log.warn("Schedule {} already recognized, skipping", schedule.getId());
            return;
        }

        CommissionPlan plan = schedule.getCommissionPlan();
        BigDecimal amount = schedule.getPlannedAmount();

        // Update schedule
        schedule.setRecognizedAmount(amount);
        schedule.setStatus("RECOGNIZED");
        scheduleRepository.save(schedule);

        // Update commission plan
        BigDecimal currentRecognized = plan.getRecognizedAmount() != null
            ? plan.getRecognizedAmount()
            : BigDecimal.ZERO;
        plan.setRecognizedAmount(currentRecognized.add(amount));

        Integer monthsRecognized = plan.getMonthsRecognized() != null ? plan.getMonthsRecognized() : 0;
        plan.setMonthsRecognized(monthsRecognized + 1);

        if (plan.getMonthsRecognized() >= (plan.getMonthsToRecognize() != null ? plan.getMonthsToRecognize() : 12)) {
            plan.setStatus(CommissionPlanStatus.RECOGNIZED);
        }
        planRepository.save(plan);

        // Record in ledger
        ledgerService.recordCommissionRecognized(
                plan.getId(),
                plan.getSalesperson().getId(),
                amount,
                "Monthly recognition - Month " + schedule.getMonth()
        );

        log.info("Revenue recognized: Plan={}, Amount={}", plan.getId(), amount);
    }

    @Override
    public int recognizeAllDue(LocalDate asOfDate) {
        log.info("Processing recognition for all due schedules as of {}", asOfDate);

        List<RecognitionSchedule> dueSchedules = scheduleRepository.findDueForRecognition(asOfDate);

        for (RecognitionSchedule schedule : dueSchedules) {
            recognizeRevenue(schedule);
        }

        log.info("Recognized {} schedules", dueSchedules.size());
        return dueSchedules.size();
    }

    @Override
    public BigDecimal calculateMonthlyAmount(CommissionPlan plan) {
        BigDecimal totalAmount = plan.getPlannedAmount();
        Integer monthsToRecognize = plan.getMonthsToRecognize() != null ? plan.getMonthsToRecognize() : 12;

        return totalAmount
                .divide(new BigDecimal(monthsToRecognize), 2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal getTotalRecognized(Long commissionPlanId) {
        return scheduleRepository.findByCommissionPlanId(commissionPlanId).stream()
                .filter(s -> "RECOGNIZED".equals(s.getStatus()) || "PAID".equals(s.getStatus()))
                .map(RecognitionSchedule::getRecognizedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal getTotalPending(Long commissionPlanId) {
        return scheduleRepository.findByCommissionPlanId(commissionPlanId).stream()
                .filter(s -> "PENDING".equals(s.getStatus()))
                .map(RecognitionSchedule::getPlannedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
