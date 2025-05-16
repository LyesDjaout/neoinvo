package com.neoinvo.invoice.service.impl;

import com.neoinvo.invoice.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendVerificationEmail(String to, String verificationLink) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            String htmlContent = """
                <html>
                    <body style="font-family: Arial, sans-serif; background-color: #f9f9f9; padding: 20px;">
                        <div style="max-width: 600px; margin: auto; background-color: #ffffff; border-radius: 8px; padding: 30px; box-shadow: 0 0 10px rgba(0,0,0,0.1);">
                            <h2 style="color: #333333;">Bienvenue sur NeoInvo 👋</h2>
                            <p style="color: #555555;">Merci de vous être inscrit. Veuillez vérifier votre adresse e-mail pour activer votre compte :</p>
                            <a href="%s" style="display: inline-block; margin-top: 20px; padding: 12px 24px; background-color: #007bff; color: white; text-decoration: none; border-radius: 5px;">Vérifier mon e-mail</a>
                            <p style="color: #888888; margin-top: 30px;">Si vous n'êtes pas à l'origine de cette inscription, ignorez simplement cet e-mail.</p>
                            <p style="color: #aaa; font-size: 12px;">NeoInvo © 2025</p>
                        </div>
                    </body>
                </html>
                """.formatted(verificationLink);

            helper.setTo(to);
            helper.setSubject("🛡️ Vérifiez votre adresse e-mail");
            helper.setText(htmlContent, true); // true = HTML

            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new IllegalStateException("Erreur lors de l'envoi de l'e-mail de vérification", e);
        }
    }

    @Override
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = HTML

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new IllegalStateException("Erreur lors de l'envoi de l'e-mail HTML", e);
        }
    }
}
