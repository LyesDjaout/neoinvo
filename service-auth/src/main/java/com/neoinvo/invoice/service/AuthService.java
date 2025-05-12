package com.neoinvo.invoice.service;

import com.neoinvo.invoice.dto.LoginRequest;
import com.neoinvo.invoice.dto.RegisterRequest;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<?> register(RegisterRequest request);
    ResponseEntity<?> login(LoginRequest request);
}
