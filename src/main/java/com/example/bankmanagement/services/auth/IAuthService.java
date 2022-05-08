package com.example.bankmanagement.services.auth;

import com.example.bankmanagement.dto.requests.auth.LoginRequest;
import com.example.bankmanagement.dto.responses.auth.AuthResponse;

public interface IAuthService {
    AuthResponse login(LoginRequest request);
}
