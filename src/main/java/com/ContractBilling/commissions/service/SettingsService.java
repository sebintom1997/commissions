package com.ContractBilling.commissions.service;

import com.ContractBilling.commissions.dto.SettingsResponse;
import com.ContractBilling.commissions.dto.UpdateSettingsRequest;

/**
 * Service interface for PolicySettings operations
 * This is a singleton - only ONE record exists
 */
public interface SettingsService {

    /**
     * Get current settings (the single record)
     */
    SettingsResponse getSettings();

    /**
     * Update settings
     */
    SettingsResponse updateSettings(UpdateSettingsRequest request, String updatedBy);

    /**
     * Initialize default settings if none exist
     * Called on application startup
     */
    void initializeDefaultSettings();
}
