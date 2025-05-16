package com.neoinvo.invoice.service;

public interface EmailService {
    void sendVerificationEmail(String to, String verificationLink);
}
