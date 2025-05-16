package com.neoinvo.invoice.service;

public interface EmailService {
    void sendVerificationEmail(String to, String verificationLink);
    void sendHtmlEmail(String to, String subject, String htmlContent);
}
