package com.ContractBilling.commissions.dto;

import com.ContractBilling.commissions.entity.ClientStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Client response (what we send back to the client/user)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientResponse {

    private Long id;
    private String name;
    private String contactPerson;
    private String email;
    private String phone;
    private String address;
    private ClientStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
