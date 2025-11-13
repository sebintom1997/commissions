package com.ContractBilling.commissions.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateSalespersonRequest {

    @NotBlank(message = "Name is mandatory")
    @Size(min =2 ,max = 100, message = "Name can have at most 100 characters")
    private String name;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email can have at most 100 characters")
    private String email;
}
