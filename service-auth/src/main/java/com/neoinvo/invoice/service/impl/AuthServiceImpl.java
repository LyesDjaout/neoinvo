package com.neoinvo.invoice.service.impl;

import com.neoinvo.invoice.config.jwt.JwtUtil;
import com.neoinvo.invoice.dto.*;
import com.neoinvo.invoice.entity.User;
import com.neoinvo.invoice.repository.UserRepository;
import com.neoinvo.invoice.service.AuthService;
import com.neoinvo.invoice.service.EmailService;
import com.neoinvo.invoice.service.SiretValidatorService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;
    private final SiretValidatorService siretValidatorService;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil,
            EmailService emailService,
            SiretValidatorService siretValidatorService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
        this.siretValidatorService = siretValidatorService;
    }

    @Override
    public ResponseEntity<?> register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            return ResponseEntity.badRequest().body("Email déjà utilisé.");
        }

        if (!siretValidatorService.isValidSiret(request.siret())) {
            return ResponseEntity.badRequest().body("SIRET invalide.");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setCompanyName(request.companyName());
        user.setSiret(request.siret());

        user.setEmailVerified(false);
        user.setVerificationToken(UUID.randomUUID().toString());

        userRepository.save(user);

        String verificationLink = "http://neoinvo.com:8080/auth/verify-email?token=" + user.getVerificationToken();

        emailService.sendVerificationEmail(user.getEmail(), verificationLink);

        return ResponseEntity.ok("Utilisateur créé. Un e-mail de vérification a été envoyé.");
    }

    @Override
    public ResponseEntity<?> login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtil.generateJwtToken(authentication);

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        if (!user.isEmailVerified()) {
            return ResponseEntity.status(401).body("Veuillez d'abord vérifier votre adresse e-mail.");
        }

        AuthResponse response = new AuthResponse();
        response.setToken(jwt);
        response.setMessage("Connexion réussie");

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> verifyEmail(String token) {
        Optional<User> userOpt = userRepository.findAll().stream()
                .filter(u -> token.equals(u.getVerificationToken()))
                .findFirst();

        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Token invalide");
        }

        User user = userOpt.get();
        user.setEmailVerified(true);
        user.setVerificationToken(null);
        userRepository.save(user);

        return ResponseEntity.ok("Email vérifié avec succès.");
    }
}