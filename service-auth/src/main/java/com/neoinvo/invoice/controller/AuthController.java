package com.neoinvo.invoice.controller;

import com.neoinvo.invoice.dto.*;
import com.neoinvo.invoice.entity.User;
import com.neoinvo.invoice.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token) {
        return authService.verifyEmail(token);
    }

    @GetMapping("/me")
    public ResponseEntity<UserMeResponse> getCurrentUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return ResponseEntity.ok(
                new UserMeResponse(
                        user.getEmail(),
                        user.getFullName(),
                        user.getCompanyName(),
                        user.getSiret(),
                        user.getEmailVerified(),
                        user.getTwoFactorEnabled(),
                        user.getCreatedAt()
                )
        );
    }

}