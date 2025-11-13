package com.ContractBilling.commissions.service.impl;

import com.ContractBilling.commissions.dto.SettingsMapper;
import com.ContractBilling.commissions.dto.SettingsResponse;
import com.ContractBilling.commissions.dto.UpdateSettingsRequest;
import com.ContractBilling.commissions.entity.PolicySettings;
import com.ContractBilling.commissions.exception.ResourceNotFoundException;
import com.ContractBilling.commissions.repository.SettingsRepository;
import com.ContractBilling.commissions.service.SettingsService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Implementation of SettingsService
 * Implements Singleton pattern - only ONE settings record exists
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SettingsServiceImpl implements SettingsService {

    private final SettingsRepository repository;
    private final SettingsMapper mapper;

    /**
     * Run after service is created - initialize default settings if needed
     */
    @PostConstruct
    public void init() {
        initializeDefaultSettings();
    }

    @Override
    @Transactional(readOnly = true)
    public SettingsResponse getSettings() {
        log.info("Fetching policy settings");

        // Get the first (and should be only) record
        List<PolicySettings> allSettings = repository.findAll();

        if (allSettings.isEmpty()) {
            throw new ResourceNotFoundException("PolicySettings", "id", 1L);
        }

        return mapper.toResponse(allSettings.get(0));
    }

    @Override
    public SettingsResponse updateSettings(UpdateSettingsRequest request, String updatedBy) {
        log.info("Updating policy settings by: {}", updatedBy);

        // Get the singleton record
        List<PolicySettings> allSettings = repository.findAll();

        if (allSettings.isEmpty()) {
            throw new ResourceNotFoundException("PolicySettings", "id", 1L);
        }

        PolicySettings entity = allSettings.get(0);

        // Update all fields
        mapper.updateEntity(entity, request, updatedBy);

        // Save
        PolicySettings updated = repository.save(entity);

        log.info("Policy settings updated successfully");

        return mapper.toResponse(updated);
    }

    @Override
    public void initializeDefaultSettings() {
        log.info("Checking if default settings need to be initialized");

        // Check if any settings exist
        long count = repository.count();

        if (count == 0) {
            log.info("No settings found. Creating default settings...");

            PolicySettings defaultSettings = new PolicySettings();

            // Set default overhead percentages
            defaultSettings.setAdminPercentage(new BigDecimal("6.00"));
            defaultSettings.setInsurancePercentage(new BigDecimal("2.00"));
            defaultSettings.setLeavePercentage(new BigDecimal("14.54"));
            defaultSettings.setPrsiPercentage(new BigDecimal("11.25"));
            defaultSettings.setPensionPercentage(new BigDecimal("1.50"));
            defaultSettings.setPensionCap(new BigDecimal("2000.00"));

            // Set default contract settings
            defaultSettings.setWeeksPerYear(45);

            // Set default commission tiers
            defaultSettings.setFirstContractCommission(new BigDecimal("15.00"));
            defaultSettings.setSecondContractCommission(new BigDecimal("10.00"));
            defaultSettings.setThirdContractCommission(new BigDecimal("8.00"));

            // Set default drawdown rules
            defaultSettings.setDrawdownMinMonth(3);
            defaultSettings.setDrawdownMaxPerQuarter(1);

            defaultSettings.setUpdatedBy("SYSTEM");

            repository.save(defaultSettings);

            log.info("Default settings created successfully");
        } else {
            log.info("Settings already exist. Count: {}", count);
        }
    }
}
