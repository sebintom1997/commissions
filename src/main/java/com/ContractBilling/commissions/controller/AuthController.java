package com.ContractBilling.commissions.controller;

import com.ContractBilling.commissions.dto.ApiResponse;
import com.ContractBilling.commissions.dto.AuthResponse;
import com.ContractBilling.commissions.dto.LoginRequest;
import com.ContractBilling.commissions.dto.RegisterRequest;
import com.ContractBilling.commissions.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication endpoints")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register new user")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse authResponse = authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<AuthResponse>builder()
                        .success(true)
                        .message("User registered successfully")
                        .data(authResponse)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @PostMapping("/login")
    @Operation(summary = "Login user")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse authResponse = authService.login(request);

        return ResponseEntity.ok(
                ApiResponse.<AuthResponse>builder()
                        .success(true)
                        .message("Login successful")
                        .data(authResponse)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}
