package com.ContractBilling.commissions.controller;

import com.ContractBilling.commissions.dto.CreateSalespersonRequest;
import com.ContractBilling.commissions.dto.SalespersonResponse;
import com.ContractBilling.commissions.dto.UpdateSalespersonRequest;
import com.ContractBilling.commissions.entity.SalespersonStatus;
import com.ContractBilling.commissions.service.SalespersonService;
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

@RestController  // Tells Spring: this handles HTTP requests and returns JSON
@RequestMapping("/api/salespeople")  // Base URL for all endpoints
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Salesperson", description = "Salesperson management APIs")
public class SalespersonController {

    private final SalespersonService salespersonService;

    // CREATE - POST /api/salespeople
    @PostMapping
    public ResponseEntity<SalespersonResponse> createSalesperson(
            @Valid @RequestBody CreateSalespersonRequest request) {

        log.info("REST request to create salesperson: {}", request.getEmail());

        SalespersonResponse response = salespersonService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)  // 201 Created
                .body(response);
    }

    // READ ALL - GET /api/salespeople (with pagination and sorting)
    @GetMapping
    @Operation(summary = "Get all salespeople", description = "Retrieves all salespeople with pagination and sorting support")
    public ResponseEntity<Page<SalespersonResponse>> getAllSalespeople(
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC)
            Pageable pageable) {
        log.info("REST request to get all salespeople with pagination");

        Page<SalespersonResponse> salespeople = salespersonService.getAll(pageable);

        return ResponseEntity.ok(salespeople);  // 200 OK
    }

    // READ ONE - GET /api/salespeople/{id}
    @GetMapping("/{id}")
    public ResponseEntity<SalespersonResponse> getSalespersonById(
            @PathVariable Long id) {

        log.info("REST request to get salesperson by ID: {}", id);

        SalespersonResponse response = salespersonService.getById(id);

        return ResponseEntity.ok(response);  // 200 OK
    }

    // UPDATE - PUT /api/salespeople/{id}
    @PutMapping("/{id}")
    public ResponseEntity<SalespersonResponse> updateSalesperson(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSalespersonRequest request) {

        log.info("REST request to update salesperson with ID: {}", id);

        SalespersonResponse response = salespersonService.update(id, request);

        return ResponseEntity.ok(response);  // 200 OK
    }

    // DELETE - DELETE /api/salespeople/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSalesperson(@PathVariable Long id) {
        log.info("REST request to delete salesperson with ID: {}", id);

        salespersonService.delete(id);

        return ResponseEntity.noContent().build();  // 204 No Content
    }

    // FIND BY EMAIL - GET /api/salespeople/email/{email}
    @GetMapping("/email/{email}")
    public ResponseEntity<SalespersonResponse> getSalespersonByEmail(
            @PathVariable String email) {

        log.info("REST request to get salesperson by email: {}", email);

        SalespersonResponse response = salespersonService.findByEmail(email);

        return ResponseEntity.ok(response);  // 200 OK
    }

    // FIND BY STATUS - GET /api/salespeople/status/{status}
    @GetMapping("/status/{status}")
    public ResponseEntity<List<SalespersonResponse>> getSalespeopleByStatus(
            @PathVariable SalespersonStatus status) {

        log.info("REST request to get salespeople by status: {}", status);

        List<SalespersonResponse> salespeople = salespersonService.findByStatus(status);

        return ResponseEntity.ok(salespeople);  // 200 OK
    }

    // SEARCH BY NAME - GET /api/salespeople/search?name=John
    @GetMapping("/search")
    public ResponseEntity<List<SalespersonResponse>> searchSalespeople(
            @RequestParam String name) {

        log.info("REST request to search salespeople by name: {}", name);

        List<SalespersonResponse> salespeople = salespersonService.searchByName(name);

        return ResponseEntity.ok(salespeople);  // 200 OK
    }
}