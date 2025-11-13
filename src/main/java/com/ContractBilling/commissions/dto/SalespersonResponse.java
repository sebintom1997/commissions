package com.ContractBilling.commissions.dto;

import lombok.Data;
import com.ContractBilling.commissions.entity.SalespersonStatus;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalespersonResponse {

    private Long id;
    private String name;
    private String email;
    private SalespersonStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
