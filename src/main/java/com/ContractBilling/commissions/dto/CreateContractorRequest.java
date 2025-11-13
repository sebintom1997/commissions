package com.ContractBilling.commissions.dto;

import com.ContractBilling.commissions.entity.ContractorType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a new Contractor
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateContractorRequest {

    @NotBlank(message = "Contractor name is mandatory")
    @Size(max = 100, message = "Name can have at most 100 characters")
    private String name;

    @Email(message = "Email should be valid")
    @Size(max = 255, message = "Email can have at most 255 characters")
    private String email;

    @Size(max = 20, message = "Phone can have at most 20 characters")
    private String phone;

    @NotNull(message = "Contractor type is mandatory")
    private ContractorType type;
}
