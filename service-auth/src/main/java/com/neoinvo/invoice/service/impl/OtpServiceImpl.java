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

        String subject = "🔐 Votre code OTP pour NeoInvo";
        String htmlBody = """
        <html>
            <body style="font-family: Arial, sans-serif; background-color: #f9f9f9; padding: 20px;">
                <div style="max-width: 600px; margin: auto; background-color: #ffffff; border-radius: 8px; padding: 30px; box-shadow: 0 0 10px rgba(0,0,0,0.1);">
                    <h2 style="color: #333333;">Code de vérification OTP 🔐</h2>
                    <p style="color: #555555;">Voici votre code de vérification à usage unique. Il est valable pendant 5 minutes :</p>
                    <div style="font-size: 24px; font-weight: bold; margin: 20px 0; color: #007bff;">%s</div>
                    <p style="color: #888888;">Si vous n'avez pas demandé ce code, vous pouvez ignorer cet e-mail.</p>
                    <p style="color: #aaa; font-size: 12px; margin-top: 30px;">NeoInvo © 2025</p>
                </div>
            </body>
        </html>
        """.formatted(otp);

        emailService.sendHtmlEmail(email, subject, htmlBody);
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
