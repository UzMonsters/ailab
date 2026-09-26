package com.ailab.common.mail;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Service
public class SmtpEmailDeliveryService implements EmailDeliveryService {

    private static final Logger log = LoggerFactory.getLogger(SmtpEmailDeliveryService.class);

    private final JavaMailSender mailSender;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Value("${app.mail.from:noreply@ailab.local}")
    private String fromAddress;

    @Value("${app.mail.from-name:AI Laboratory}")
    private String fromName;

    public SmtpEmailDeliveryService(@Autowired(required = false) JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendVerificationEmail(String recipientEmail, String username, String rawToken, Duration ttl) {
        String verificationUrl = buildVerificationUrl(rawToken);
        long minutes = ttl != null ? ttl.toMinutes() : 30;
        String subject = "Verify your email - AI Laboratory";

        String htmlContent = "<div style=\"font-family: sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e2e8f0; rounded: 8px;\">"
                + "<h2 style=\"color: #4f46e5;\">Welcome to AI Laboratory</h2>"
                + "<p>Hello <strong>" + escapeHtml(username) + "</strong>,</p>"
                + "<p>Thank you for registering. Please click the button below to verify your email address and activate your account:</p>"
                + "<p style=\"margin: 24px 0;\"><a href=\"" + verificationUrl + "\" style=\"background-color: #7c3aed; color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px; font-weight: bold; display: inline-block;\">Verify Email</a></p>"
                + "<p style=\"font-size: 13px; color: #64748b;\">This link will expire in " + minutes + " minutes. If you did not create an account, you can safely ignore this email.</p>"
                + "<hr style=\"border: none; border-top: 1px solid #e2e8f0; margin: 20px 0;\" />"
                + "<p style=\"font-size: 11px; color: #94a3b8;\">If the button doesn't work, copy and paste this link into your browser:<br/><a href=\"" + verificationUrl + "\">" + verificationUrl + "</a></p>"
                + "</div>";

        String textContent = "Welcome to AI Laboratory\n\n"
                + "Hello " + username + ",\n\n"
                + "Thank you for registering. Please verify your email address by visiting the link below:\n\n"
                + verificationUrl + "\n\n"
                + "This link will expire in " + minutes + " minutes. If you did not create an account, you can safely ignore this email.\n";

        send(recipientEmail, subject, htmlContent, textContent);
    }

    @Override
    public void sendEmailChangeVerification(String recipientEmail, String username, String rawToken, Duration ttl) {
        String verificationUrl = buildVerificationUrl(rawToken);
        long minutes = ttl != null ? ttl.toMinutes() : 30;
        String subject = "Confirm your new email address - AI Laboratory";

        String htmlContent = "<div style=\"font-family: sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e2e8f0; rounded: 8px;\">"
                + "<h2 style=\"color: #4f46e5;\">Email Change Request</h2>"
                + "<p>Hello <strong>" + escapeHtml(username) + "</strong>,</p>"
                + "<p>You requested to update your AI Laboratory email address to <strong>" + escapeHtml(recipientEmail) + "</strong>. Please confirm this change:</p>"
                + "<p style=\"margin: 24px 0;\"><a href=\"" + verificationUrl + "\" style=\"background-color: #7c3aed; color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px; font-weight: bold; display: inline-block;\">Confirm New Email</a></p>"
                + "<p style=\"font-size: 13px; color: #64748b;\">This link will expire in " + minutes + " minutes. If you did not request this change, please contact support immediately.</p>"
                + "<hr style=\"border: none; border-top: 1px solid #e2e8f0; margin: 20px 0;\" />"
                + "<p style=\"font-size: 11px; color: #94a3b8;\">Link:<br/><a href=\"" + verificationUrl + "\">" + verificationUrl + "</a></p>"
                + "</div>";

        String textContent = "Email Change Request\n\n"
                + "Hello " + username + ",\n\n"
                + "You requested to update your email address to " + recipientEmail + ". Please confirm by visiting:\n\n"
                + verificationUrl + "\n\n"
                + "This link will expire in " + minutes + " minutes.\n";

        send(recipientEmail, subject, htmlContent, textContent);
    }

    private String buildVerificationUrl(String rawToken) {
        String base = frontendUrl != null ? frontendUrl.replaceAll("/+$", "") : "http://localhost:3000";
        return base + "/auth/verify?token=" + rawToken;
    }

    private void send(String recipient, String subject, String htmlContent, String textContent) {
        if (mailSender == null) {
            log.warn("JavaMailSender is not available. Skipping email delivery to recipient: {}", maskEmail(recipient));
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setFrom(new InternetAddress(fromAddress, fromName, StandardCharsets.UTF_8.name()));
            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(textContent, htmlContent);
            mailSender.send(message);
            log.info("Verification email sent to recipient: {}", maskEmail(recipient));
        } catch (Exception ex) {
            log.error("Failed to send email to recipient {}: {}", maskEmail(recipient), ex.getMessage());
        }
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return "***";
        int at = email.indexOf('@');
        String prefix = at > 2 ? email.substring(0, 2) + "***" : "***";
        return prefix + email.substring(at);
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}