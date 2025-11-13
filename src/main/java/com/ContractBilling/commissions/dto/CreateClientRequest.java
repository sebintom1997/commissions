package com.ContractBilling.commissions.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a new Client
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateClientRequest {

    @NotBlank(message = "Client name is mandatory")
    @Size(max = 100, message = "Client name can have at most 100 characters")
    private String name;

    @Size(max = 100, message = "Contact person name can have at most 100 characters")
    private String contactPerson;

    @Email(message = "Email should be valid")
    @Size(max = 255, message = "Email can have at most 255 characters")
    private String email;

    @Size(max = 20, message = "Phone can have at most 20 characters")
    private String phone;

    private String address;
}
