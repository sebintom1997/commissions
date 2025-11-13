package com.ContractBilling.commissions.service;

import com.ContractBilling.commissions.dto.AuthResponse;
import com.ContractBilling.commissions.dto.LoginRequest;
import com.ContractBilling.commissions.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
