package com.neoinvo.invoice.service.impl;

import com.neoinvo.invoice.service.EmailService;
import com.neoinvo.invoice.service.OtpService;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpServiceImpl implements OtpService {

    private final Map<String, OtpData> otpStorage = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();
    private static final int OTP_LENGTH = 6;
    private static final long OTP_VALID_DURATION_SECONDS = 300; // 5 minutes

    private final EmailService emailService;

    public OtpServiceImpl(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void generateOtp(String email) {
        String otp = String.format("%06d", random.nextInt(1_000_000));
        otpStorage.put(email, new OtpData(otp, Instant.now().plusSeconds(OTP_VALID_DURATION_SECONDS)));

        // Envoi de l'OTP par email
        String subject = "Votre code OTP pour NeoInvo";
        String body = String.format("""
            Bonjour,

            Voici votre code de vérification OTP valable 5 minutes :

            %s

            Si vous n'avez pas demandé ce code, ignorez cet email.

            Cordialement,
            L'équipe NeoInvo
            """, otp);

        emailService.sendSimpleEmail(email, subject, body);
    }

    @Override
    public boolean verifyOtp(String email, String otp) {
        OtpData data = otpStorage.get(email);
        if (data == null) return false;
        if (Instant.now().isAfter(data.expiration)) {
            otpStorage.remove(email);
            return false; // OTP expiré
        }
        boolean valid = data.otp.equals(otp);
        if (valid) otpStorage.remove(email);
        return valid;
    }

    private static class OtpData {
        final String otp;
        final Instant expiration;
        OtpData(String otp, Instant expiration) {
            this.otp = otp;
            this.expiration = expiration;
        }
    }
}
