package com.ContractBilling.commissions;

import com.ContractBilling.commissions.dto.CreatePlacementRequest;
import com.ContractBilling.commissions.dto.PlacementResponse;
import com.ContractBilling.commissions.entity.*;
import com.ContractBilling.commissions.repository.ClientRepository;
import com.ContractBilling.commissions.repository.ContractorRepository;
import com.ContractBilling.commissions.repository.SalespersonRepository;
import com.ContractBilling.commissions.repository.SettingsRepository;
import com.ContractBilling.commissions.service.PlacementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class PlacementServiceIntegrationTest {

    @Autowired
    private PlacementService placementService;

    @Autowired
    private SalespersonRepository salespersonRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ContractorRepository contractorRepository;

    @Autowired
    private SettingsRepository settingsRepository;

    private Salesperson salesperson;
    private Client client;
    private Contractor contractor;

    @BeforeEach
    public void setUp() {
        // Create test salesperson
        salesperson = new Salesperson();
        salesperson.setName("John Doe");
        salesperson.setEmail("john@example.com");
        salespersonRepository.save(salesperson);

        // Create test client
        client = new Client();
        client.setName("ABC Corp");
        client.setEmail("contact@abc.com");
        clientRepository.save(client);

        // Create test contractor
        contractor = new Contractor();
        contractor.setName("Jane Smith");
        contractor.setEmail("jane@example.com");
        contractorRepository.save(contractor);
    }

    @Test
    public void testCreateContractorPlacement() {
        CreatePlacementRequest request = new CreatePlacementRequest();
        request.setSalespersonId(salesperson.getId());
        request.setClientId(client.getId());
        request.setContractorId(contractor.getId());
        request.setPlacementType(PlacementType.CONTRACTOR);
        request.setStartDate(java.time.LocalDate.now());
        request.setBillRate(new BigDecimal("75.00"));
        request.setHourlyPayRate(new BigDecimal("35.00"));
        request.setHoursPerWeek(new BigDecimal("40"));
        request.setWeeksPerYear(52);

        PlacementResponse response = placementService.create(request);

        assertNotNull(response.getId());
        assertEquals(PlacementType.CONTRACTOR, response.getPlacementType());
        assertNotNull(response.getCommissionTotal());
        assertTrue(response.getCommissionTotal().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    public void testCreatePermanentPlacement() {
        CreatePlacementRequest request = new CreatePlacementRequest();
        request.setSalespersonId(salesperson.getId());
        request.setClientId(client.getId());
        request.setContractorId(contractor.getId());
        request.setPlacementType(PlacementType.PERMANENT);
        request.setStartDate(java.time.LocalDate.now());
        request.setAnnualSalary(new BigDecimal("60000"));
        request.setPlacementFee(new BigDecimal("5000"));

        PlacementResponse response = placementService.create(request);

        assertNotNull(response.getId());
        assertEquals(PlacementType.PERMANENT, response.getPlacementType());
        assertNotNull(response.getCommissionTotal());
    }
}
