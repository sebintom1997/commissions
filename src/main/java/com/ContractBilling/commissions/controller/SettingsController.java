package com.ContractBilling.commissions.controller;

import com.ContractBilling.commissions.dto.SettingsResponse;
import com.ContractBilling.commissions.dto.UpdateSettingsRequest;
import com.ContractBilling.commissions.service.SettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for PolicySettings operations
 * This is a SINGLETON - only GET and PUT operations allowed
 * No POST (create) or DELETE operations
 */
@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Settings", description = "Policy settings management APIs (Singleton)")
public class SettingsController {

    private final SettingsService settingsService;

    /**
     * GET - Retrieve current policy settings
     */
    @GetMapping
    @Operation(summary = "Get policy settings", description = "Retrieves the current system-wide policy settings (singleton)")
    public ResponseEntity<SettingsResponse> getSettings() {
        log.info("REST request to get policy settings");

        SettingsResponse response = settingsService.getSettings();

        return ResponseEntity.ok(response);
    }

    /**
     * PUT - Update policy settings
     * Note: Admin authorization will be added later (Day 22-23)
     */
    @PutMapping
    @Operation(summary = "Update policy settings", description = "Updates the system-wide policy settings (admin only)")
    public ResponseEntity<SettingsResponse> updateSettings(
            @Valid @RequestBody UpdateSettingsRequest request) {

        log.info("REST request to update policy settings");

        // TODO: Get username from security context (will be added in Day 22-23)
        String updatedBy = "ADMIN"; // Placeholder

        SettingsResponse response = settingsService.updateSettings(request, updatedBy);

        return ResponseEntity.ok(response);
    }
}
