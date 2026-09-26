package com.ailab.storage.upload;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Objects;
import java.util.UUID;

@Service
public class UploadTicketService {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private final UploadTicketStore store;

    public UploadTicketService(UploadTicketStore store) {
        this.store = store;
    }

    @Transactional
    public IssuedUploadTicket issue(UploadTicketIssueCommand command) {
        if (command.assetId() == null || command.assetId().isBlank()) {
            throw new IllegalArgumentException("assetId is required");
        }
        if (command.actorId() == null || command.actorId().isBlank()) {
            throw new IllegalArgumentException("actorId is required");
        }
        if (command.scope() == null) {
            throw new IllegalArgumentException("scope is required");
        }
        if (command.storageKey() == null || command.storageKey().isBlank()) {
            throw new IllegalArgumentException("storageKey is required");
        }
        if (command.allowedMime() == null || command.allowedMime().isBlank()) {
            throw new IllegalArgumentException("allowedMime is required");
        }
        if (command.maxSizeBytes() <= 0) {
            throw new IllegalArgumentException("maxSizeBytes must be positive");
        }
        Duration ttl = command.ttl() != null ? command.ttl() : Duration.ofMinutes(15);
        if (ttl.isNegative() || ttl.isZero()) {
            throw new IllegalArgumentException("ttl must be positive");
        }
        String token = secureToken();
        Instant expiresAt = Instant.now().plus(ttl);
        UploadTicketEntity ticket = new UploadTicketEntity(
                "upl_" + UUID.randomUUID().toString().replace("-", ""),
                sha256Hex(token),
                command.assetId(),
                command.actorId(),
                command.scope(),
                command.storageKey(),
                normalizeMime(command.allowedMime()),
                command.maxSizeBytes(),
                normalizeChecksum(command.expectedChecksum()),
                expiresAt,
                command.workspaceId(),
                command.previewId(),
                command.variant());
        store.save(ticket);
        return new IssuedUploadTicket(command.assetId(), token, command.storageKey(), expiresAt);
    }

    @Transactional
    public UploadTicketEntity claimForUpload(UploadTicketClaimCommand command) {
        UploadTicketEntity ticket = findByRawToken(command.token());
        validateContext(ticket, command.assetId(), command.actorId(), command.scope(),
                command.workspaceId(), command.previewId(), command.variant());
        Instant now = Instant.now();
        if (!ticket.getStatus().equals(UploadTicketStatus.ISSUED)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "UPLOAD_TICKET_ALREADY_USED: Upload ticket is already used");
        }
        if (!ticket.getExpiresAt().isAfter(now)) {
            ticket.setStatus(UploadTicketStatus.EXPIRED);
            store.save(ticket);
            throw new ResponseStatusException(HttpStatus.GONE, "UPLOAD_TICKET_EXPIRED: Upload ticket has expired");
        }
        int claimed = store.claimIssued(ticket.getId(), now);
        if (claimed != 1) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "UPLOAD_TICKET_ALREADY_USED: Upload ticket is already used");
        }
        return store.findByTokenHash(ticket.getTokenHash()).orElseThrow();
    }

    @Transactional
    public UploadTicketEntity markUploaded(UploadTicketEntity ticket, String actualChecksum, long actualSizeBytes, String actualMime) {
        if (!ticket.getStatus().equals(UploadTicketStatus.UPLOADING)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "UPLOAD_TICKET_STATE_CONFLICT: Ticket is not uploading");
        }
        ticket.setActualChecksum(normalizeChecksum(actualChecksum));
        ticket.setActualSizeBytes(actualSizeBytes);
        ticket.setActualMime(normalizeMime(actualMime));
        ticket.setUploadedAt(Instant.now());
        ticket.setStatus(UploadTicketStatus.UPLOADED);
        return store.save(ticket);
    }

    @Transactional(readOnly = true)
    public UploadTicketEntity validateForComplete(String assetId, String actorId, UploadScope scope,
                                                  String workspaceId, String previewId, String variant) {
        UploadTicketEntity ticket = store.findLatest(assetId, actorId, scope, workspaceId, previewId, variant)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                        "UPLOAD_INCOMPLETE: Upload has not completed"));
        if (!ticket.getStatus().equals(UploadTicketStatus.UPLOADED) && !ticket.getStatus().equals(UploadTicketStatus.COMPLETED)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "UPLOAD_INCOMPLETE: Upload has not completed");
        }
        return ticket;
    }

    @Transactional(readOnly = true)
    public UploadTicketEntity findLatestByAssetId(String assetId) {
        return store.findLatestByAssetId(assetId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "STORAGE_OBJECT_NOT_FOUND: Upload metadata was not found"));
    }

    @Transactional
    public UploadTicketEntity markCompleted(UploadTicketEntity ticket) {
        if (ticket.getStatus().equals(UploadTicketStatus.COMPLETED)) {
            return ticket;
        }
        if (!ticket.getStatus().equals(UploadTicketStatus.UPLOADED)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "UPLOAD_TICKET_STATE_CONFLICT: Ticket is not uploaded");
        }
        ticket.setCompletedAt(Instant.now());
        ticket.setStatus(UploadTicketStatus.COMPLETED);
        return store.save(ticket);
    }

    @Transactional(readOnly = true)
    public UploadTicketEntity findByRawToken(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "UPLOAD_TICKET_INVALID: Upload ticket is required");
        }
        return store.findByTokenHash(sha256Hex(rawToken.trim()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "UPLOAD_TICKET_INVALID: Upload ticket is invalid"));
    }

    public String normalizeChecksum(String checksum) {
        if (checksum == null || checksum.isBlank()) {
            return null;
        }
        String clean = checksum.trim().toLowerCase();
        return clean.startsWith("sha256:") ? clean : "sha256:" + clean;
    }

    private void validateContext(UploadTicketEntity ticket, String assetId, String actorId, UploadScope scope,
                                 String workspaceId, String previewId, String variant) {
        if (!Objects.equals(ticket.getActorId(), actorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "UPLOAD_TICKET_ACTOR_MISMATCH: Upload ticket belongs to a different actor");
        }
        if (ticket.getScope() != scope) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "UPLOAD_TICKET_SCOPE_MISMATCH: Upload ticket has the wrong scope");
        }
        if (!Objects.equals(ticket.getAssetId(), assetId)
                || !Objects.equals(ticket.getWorkspaceId(), workspaceId)
                || !Objects.equals(ticket.getPreviewId(), previewId)
                || !Objects.equals(ticket.getVariant(), variant)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "UPLOAD_TICKET_TARGET_MISMATCH: Upload ticket has the wrong target");
        }
    }

    private String secureToken() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public static String sha256Hex(String value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }

    private String normalizeMime(String mime) {
        if (mime == null) {
            return null;
        }
        return mime.split(";")[0].trim().toLowerCase();
    }
}
