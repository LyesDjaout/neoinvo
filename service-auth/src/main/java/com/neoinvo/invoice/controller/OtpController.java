package com.neoinvo.invoice.controller;

import com.neoinvo.invoice.config.jwt.JwtUtil;
import com.neoinvo.invoice.dto.OTPRequest;
import com.neoinvo.invoice.dto.OTPResponse;
import com.neoinvo.invoice.service.OtpService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class OtpController {

    private final OtpService otpService;
    private final JwtUtil jwtUtil;

    public OtpController(OtpService otpService, JwtUtil jwtUtil) {
        this.otpService = otpService;
        this.jwtUtil = jwtUtil;
    }

    // Générer et envoyer OTP (ex: après login si 2FA activé)
    @PostMapping("/generate-otp")
    public ResponseEntity<Void> generateOtp(@RequestParam String email) {
        otpService.generateOtp(email);
        return ResponseEntity.ok().build();
    }

    // Vérifier OTP envoyé par utilisateur
    @PostMapping("/verify-otp")
    public ResponseEntity<OTPResponse> verifyOtp(@RequestBody OTPRequest request) {
        boolean verified = otpService.verifyOtp(request.email(), request.otp());
        if (verified) {
            String jwt = jwtUtil.generateJwtTokenForEmail(request.email());

            OTPResponse response = new OTPResponse(true, "OTP vérifié avec succès", jwt);

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body(new OTPResponse(false, "OTP invalide ou expiré"));
        }
    }
}
