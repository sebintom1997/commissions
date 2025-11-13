package com.ContractBilling.commissions.controller;

import com.ContractBilling.commissions.dto.ContractorResponse;
import com.ContractBilling.commissions.dto.CreateContractorRequest;
import com.ContractBilling.commissions.dto.UpdateContractorRequest;
import com.ContractBilling.commissions.entity.ContractorStatus;
import com.ContractBilling.commissions.entity.ContractorType;
import com.ContractBilling.commissions.service.ContractorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Contractor operations
 */
@RestController
@RequestMapping("/api/contractors")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Contractor", description = "Contractor management APIs")
public class ContractorController {

    private final ContractorService contractorService;

    /**
     * CREATE - POST /api/contractors
     */
    @PostMapping
    @Operation(summary = "Create a new contractor", description = "Creates a new contractor in the system")
    public ResponseEntity<ContractorResponse> createContractor(
            @Valid @RequestBody CreateContractorRequest request) {

        log.info("REST request to create contractor: {}", request.getName());

        ContractorResponse response = contractorService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * READ ALL - GET /api/contractors (with pagination and sorting)
     */
    @GetMapping
    @Operation(summary = "Get all contractors", description = "Retrieves all contractors with pagination and sorting support")
    public ResponseEntity<Page<ContractorResponse>> getAllContractors(
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC)
            Pageable pageable) {
        log.info("REST request to get all contractors with pagination");

        Page<ContractorResponse> contractors = contractorService.getAll(pageable);

        return ResponseEntity.ok(contractors);
    }

    /**
     * READ ONE - GET /api/contractors/{id}
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get contractor by ID", description = "Retrieves a specific contractor by their ID")
    public ResponseEntity<ContractorResponse> getContractorById(@PathVariable Long id) {
        log.info("REST request to get contractor by ID: {}", id);

        ContractorResponse response = contractorService.getById(id);

        return ResponseEntity.ok(response);
    }

    /**
     * UPDATE - PUT /api/contractors/{id}
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update contractor", description = "Updates an existing contractor")
    public ResponseEntity<ContractorResponse> updateContractor(
            @PathVariable Long id,
            @Valid @RequestBody UpdateContractorRequest request) {

        log.info("REST request to update contractor with ID: {}", id);

        ContractorResponse response = contractorService.update(id, request);

        return ResponseEntity.ok(response);
    }

    /**
     * DELETE - DELETE /api/contractors/{id}
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete contractor", description = "Deletes a contractor from the system")
    public ResponseEntity<Void> deleteContractor(@PathVariable Long id) {
        log.info("REST request to delete contractor with ID: {}", id);

        contractorService.delete(id);

        return ResponseEntity.noContent().build();
    }

    /**
     * FIND BY EMAIL - GET /api/contractors/email/{email}
     */
    @GetMapping("/email/{email}")
    @Operation(summary = "Get contractor by email", description = "Finds a contractor by their email address")
    public ResponseEntity<ContractorResponse> getContractorByEmail(@PathVariable String email) {
        log.info("REST request to get contractor by email: {}", email);

        ContractorResponse response = contractorService.findByEmail(email);

        return ResponseEntity.ok(response);
    }

    /**
     * FIND BY STATUS - GET /api/contractors/status/{status}
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "Get contractors by status", description = "Retrieves all contractors with a specific status")
    public ResponseEntity<List<ContractorResponse>> getContractorsByStatus(@PathVariable ContractorStatus status) {
        log.info("REST request to get contractors by status: {}", status);

        List<ContractorResponse> contractors = contractorService.findByStatus(status);

        return ResponseEntity.ok(contractors);
    }

    /**
     * FIND BY TYPE - GET /api/contractors/type/{type}
     */
    @GetMapping("/type/{type}")
    @Operation(summary = "Get contractors by type", description = "Retrieves all contractors with a specific type")
    public ResponseEntity<List<ContractorResponse>> getContractorsByType(@PathVariable ContractorType type) {
        log.info("REST request to get contractors by type: {}", type);

        List<ContractorResponse> contractors = contractorService.findByType(type);

        return ResponseEntity.ok(contractors);
    }

    /**
     * SEARCH BY NAME - GET /api/contractors/search?name=keyword
     */
    @GetMapping("/search")
    @Operation(summary = "Search contractors by name", description = "Searches for contractors by name (case-insensitive partial match)")
    public ResponseEntity<List<ContractorResponse>> searchContractors(@RequestParam String name) {
        log.info("REST request to search contractors by name: {}", name);

        List<ContractorResponse> contractors = contractorService.searchByName(name);

        return ResponseEntity.ok(contractors);
    }
}
