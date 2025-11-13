package com.ContractBilling.commissions.dto;

import com.ContractBilling.commissions.entity.ContractorStatus;
import com.ContractBilling.commissions.entity.ContractorType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Contractor response (what we send back to the client/user)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContractorResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private ContractorType type;
    private ContractorStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
