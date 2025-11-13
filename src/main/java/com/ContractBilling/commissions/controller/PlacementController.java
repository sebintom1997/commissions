package com.ContractBilling.commissions.controller;

import com.ContractBilling.commissions.dto.ApiResponse;
import com.ContractBilling.commissions.dto.CreatePlacementRequest;
import com.ContractBilling.commissions.dto.PlacementResponse;
import com.ContractBilling.commissions.dto.UpdatePlacementRequest;
import com.ContractBilling.commissions.entity.PlacementStatus;
import com.ContractBilling.commissions.entity.PlacementType;
import com.ContractBilling.commissions.service.PlacementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.data.domain.Sort.Direction.DESC;

/**
 * REST Controller for Placement operations
 */
@RestController
@RequestMapping("/api/placements")
@RequiredArgsConstructor
@Tag(name = "Placements", description = "API for managing placements (contractor and permanent)")
public class PlacementController {

    private final PlacementService placementService;

    @PostMapping
    @Operation(summary = "Create a new placement", description = "Creates a new placement record")
    public ResponseEntity<ApiResponse<PlacementResponse>> createPlacement(
            @Valid @RequestBody CreatePlacementRequest request) {

        PlacementResponse created = placementService.create(request);

        ApiResponse<PlacementResponse> response = ApiResponse.<PlacementResponse>builder()
                .success(true)
                .message("Placement created successfully")
                .data(created)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all placements with pagination", description = "Retrieves all placements with pagination and sorting")
    public ResponseEntity<Page<PlacementResponse>> getAllPlacements(
            @PageableDefault(size = 20, sort = "createdAt", direction = DESC)
            Pageable pageable) {

        return ResponseEntity.ok(placementService.getAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get placement by ID", description = "Retrieves a specific placement by its ID")
    public ResponseEntity<ApiResponse<PlacementResponse>> getPlacementById(@PathVariable Long id) {

        PlacementResponse placement = placementService.getById(id);

        ApiResponse<PlacementResponse> response = ApiResponse.<PlacementResponse>builder()
                .success(true)
                .message("Placement retrieved successfully")
                .data(placement)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update placement", description = "Updates an existing placement")
    public ResponseEntity<ApiResponse<PlacementResponse>> updatePlacement(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePlacementRequest request) {

        PlacementResponse updated = placementService.update(id, request);

        ApiResponse<PlacementResponse> response = ApiResponse.<PlacementResponse>builder()
                .success(true)
                .message("Placement updated successfully")
                .data(updated)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete placement", description = "Deletes a placement by ID")
    public ResponseEntity<ApiResponse<Void>> deletePlacement(@PathVariable Long id) {

        placementService.delete(id);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Placement deleted successfully")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/salesperson/{salespersonId}")
    @Operation(summary = "Get placements by salesperson", description = "Retrieves all placements for a specific salesperson")
    public ResponseEntity<List<PlacementResponse>> getPlacementsBySalesperson(
            @PathVariable Long salespersonId) {

        return ResponseEntity.ok(placementService.findBySalespersonId(salespersonId));
    }

    @GetMapping("/client/{clientId}")
    @Operation(summary = "Get placements by client", description = "Retrieves all placements for a specific client")
    public ResponseEntity<List<PlacementResponse>> getPlacementsByClient(
            @PathVariable Long clientId) {

        return ResponseEntity.ok(placementService.findByClientId(clientId));
    }

    @GetMapping("/contractor/{contractorId}")
    @Operation(summary = "Get placements by contractor", description = "Retrieves all placements for a specific contractor")
    public ResponseEntity<List<PlacementResponse>> getPlacementsByContractor(
            @PathVariable Long contractorId) {

        return ResponseEntity.ok(placementService.findByContractorId(contractorId));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get placements by status", description = "Retrieves all placements with a specific status")
    public ResponseEntity<List<PlacementResponse>> getPlacementsByStatus(
            @PathVariable PlacementStatus status) {

        return ResponseEntity.ok(placementService.findByStatus(status));
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get placements by type", description = "Retrieves all placements of a specific type (CONTRACTOR or PERMANENT)")
    public ResponseEntity<List<PlacementResponse>> getPlacementsByType(
            @PathVariable PlacementType type) {

        return ResponseEntity.ok(placementService.findByType(type));
    }
}
