package com.ailab.common.mail;

import java.time.Duration;

public interface EmailDeliveryService {
    void sendVerificationEmail(String recipientEmail, String username, String rawToken, Duration ttl);
    void sendEmailChangeVerification(String recipientEmail, String username, String rawToken, Duration ttl);
}