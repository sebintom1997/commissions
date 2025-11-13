package com.ContractBilling.commissions.controller;

import com.ContractBilling.commissions.dto.ClientResponse;
import com.ContractBilling.commissions.dto.CreateClientRequest;
import com.ContractBilling.commissions.dto.UpdateClientRequest;
import com.ContractBilling.commissions.entity.ClientStatus;
import com.ContractBilling.commissions.service.ClientService;
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
 * REST Controller for Client operations
 */
@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Client", description = "Client management APIs")
public class ClientController {

    private final ClientService clientService;

    /**
     * CREATE - POST /api/clients
     */
    @PostMapping
    @Operation(summary = "Create a new client", description = "Creates a new client in the system")
    public ResponseEntity<ClientResponse> createClient(
            @Valid @RequestBody CreateClientRequest request) {

        log.info("REST request to create client: {}", request.getName());

        ClientResponse response = clientService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * READ ALL - GET /api/clients (with pagination and sorting)
     */
    @GetMapping
    @Operation(summary = "Get all clients", description = "Retrieves all clients with pagination and sorting support")
    public ResponseEntity<Page<ClientResponse>> getAllClients(
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC)
            Pageable pageable) {
        log.info("REST request to get all clients with pagination");

        Page<ClientResponse> clients = clientService.getAll(pageable);

        return ResponseEntity.ok(clients);
    }

    /**
     * READ ONE - GET /api/clients/{id}
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get client by ID", description = "Retrieves a specific client by their ID")
    public ResponseEntity<ClientResponse> getClientById(@PathVariable Long id) {
        log.info("REST request to get client by ID: {}", id);

        ClientResponse response = clientService.getById(id);

        return ResponseEntity.ok(response);
    }

    /**
     * UPDATE - PUT /api/clients/{id}
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update client", description = "Updates an existing client")
    public ResponseEntity<ClientResponse> updateClient(
            @PathVariable Long id,
            @Valid @RequestBody UpdateClientRequest request) {

        log.info("REST request to update client with ID: {}", id);

        ClientResponse response = clientService.update(id, request);

        return ResponseEntity.ok(response);
    }

    /**
     * DELETE - DELETE /api/clients/{id}
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete client", description = "Deletes a client from the system")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        log.info("REST request to delete client with ID: {}", id);

        clientService.delete(id);

        return ResponseEntity.noContent().build();
    }

    /**
     * FIND BY EMAIL - GET /api/clients/email/{email}
     */
    @GetMapping("/email/{email}")
    @Operation(summary = "Get client by email", description = "Finds a client by their email address")
    public ResponseEntity<ClientResponse> getClientByEmail(@PathVariable String email) {
        log.info("REST request to get client by email: {}", email);

        ClientResponse response = clientService.findByEmail(email);

        return ResponseEntity.ok(response);
    }

    /**
     * FIND BY STATUS - GET /api/clients/status/{status}
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "Get clients by status", description = "Retrieves all clients with a specific status")
    public ResponseEntity<List<ClientResponse>> getClientsByStatus(@PathVariable ClientStatus status) {
        log.info("REST request to get clients by status: {}", status);

        List<ClientResponse> clients = clientService.findByStatus(status);

        return ResponseEntity.ok(clients);
    }

    /**
     * SEARCH BY NAME - GET /api/clients/search?name=keyword
     */
    @GetMapping("/search")
    @Operation(summary = "Search clients by name", description = "Searches for clients by name (case-insensitive partial match)")
    public ResponseEntity<List<ClientResponse>> searchClients(@RequestParam String name) {
        log.info("REST request to search clients by name: {}", name);

        List<ClientResponse> clients = clientService.searchByName(name);

        return ResponseEntity.ok(clients);
    }
}
