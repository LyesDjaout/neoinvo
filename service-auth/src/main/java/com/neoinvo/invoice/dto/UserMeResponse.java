package com.neoinvo.invoice.dto;

import java.time.LocalDateTime;

public record UserMeResponse(
        String email,
        String fullName,
        String companyName,
        String siret,
        boolean emailVerified,
        boolean twoFactorEnabled,
        LocalDateTime createdAt
) {}