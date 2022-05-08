package com.example.bankmanagement.services.auth;

import com.example.bankmanagement.dto.requests.auth.LoginRequest;
import com.example.bankmanagement.dto.responses.auth.AuthResponse;
import com.example.bankmanagement.repositories.UserRepository;
import com.example.bankmanagement.security.JWTUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements IAuthService {

    final UserRepository userRepository;
    final JWTUtil jwtUtil;
    private final AuthenticationManager authManager;

    public AuthService(UserRepository userRepository, JWTUtil jwtUtil, AuthenticationManager authManager) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.authManager = authManager;
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        UsernamePasswordAuthenticationToken authInputToken =
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

        authManager.authenticate(authInputToken);

        return new AuthResponse(jwtUtil.generateToken(request.getEmail()));
    }
}
