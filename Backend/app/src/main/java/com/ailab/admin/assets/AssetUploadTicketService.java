package com.ailab.admin.assets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AssetUploadTicketService {

    private final byte[] secret;
    private final Set<String> usedTickets = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public record UploadTicket(
            String ticketId,
            String assetId,
            String actorId,
            String scope,
            String allowedMime,
            long maxSizeBytes,
            String expectedChecksum,
            Instant expiresAt
    ) {}

    public AssetUploadTicketService(@Value("${app.security.jwt-secret:local-dev-jwt-secret-key-must-be-at-least-256-bits-long-32-bytes}") String secret) {
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
    }

    public String issueTicket(String assetId, String actorId, String scope, String allowedMime, long maxSizeBytes, String expectedChecksum, Instant expiresAt) {
        String ticketId = UUID.randomUUID().toString();
        String payload = String.join("|",
                ticketId,
                assetId != null ? assetId : "",
                actorId != null ? actorId : "",
                scope != null ? scope : "general",
                allowedMime != null ? allowedMime : "*/*",
                String.valueOf(maxSizeBytes),
                expectedChecksum != null ? expectedChecksum : "",
                String.valueOf(expiresAt != null ? expiresAt.getEpochSecond() : Instant.now().plusSeconds(3600).getEpochSecond())
        );
        String signature = sign(payload);
        return Base64.getUrlEncoder().withoutPadding().encodeToString((payload + ":" + signature).getBytes(StandardCharsets.UTF_8));
    }

    public UploadTicket validateAndConsumeTicket(String rawToken, String expectedAssetId) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new IllegalArgumentException("Upload ticket is required");
        }
        try {
            String decoded = new String(Base64.getUrlDecoder().decode(rawToken.trim()), StandardCharsets.UTF_8);
            int lastColon = decoded.lastIndexOf(':');
            if (lastColon <= 0) {
                throw new IllegalArgumentException("Invalid upload ticket format");
            }
            String payload = decoded.substring(0, lastColon);
            String signature = decoded.substring(lastColon + 1);

            if (!MessageDigest.isEqual(sign(payload).getBytes(StandardCharsets.UTF_8), signature.getBytes(StandardCharsets.UTF_8))) {
                throw new IllegalArgumentException("Invalid upload ticket signature");
            }

            String[] parts = payload.split("\\|", -1);
            if (parts.length < 8) {
                throw new IllegalArgumentException("Invalid upload ticket payload");
            }

            String ticketId = parts[0];
            String assetId = parts[1];
            String actorId = parts[2];
            String scope = parts[3];
            String allowedMime = parts[4];
            long maxSizeBytes = Long.parseLong(parts[5]);
            String expectedChecksum = parts[6];
            Instant expiresAt = Instant.ofEpochSecond(Long.parseLong(parts[7]));

            if (Instant.now().isAfter(expiresAt)) {
                throw new IllegalStateException("Upload ticket has expired");
            }
            if (expectedAssetId != null && !expectedAssetId.equals(assetId)) {
                throw new IllegalArgumentException("Upload ticket does not match target asset ID");
            }
            if (!usedTickets.add(ticketId)) {
                throw new IllegalStateException("Upload ticket has already been used");
            }

            return new UploadTicket(ticketId, assetId, actorId, scope, allowedMime, maxSizeBytes, expectedChecksum, expiresAt);
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Malformed upload ticket", e);
        }
    }

    private String sign(String payload) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret, "HmacSHA256"));
            byte[] raw = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(raw);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to calculate HMAC signature", e);
        }
    }

    public static String detectMimeType(byte[] data) {
        return com.ailab.storage.upload.UploadContentInspector.detectMimeType(data);
    }

}
