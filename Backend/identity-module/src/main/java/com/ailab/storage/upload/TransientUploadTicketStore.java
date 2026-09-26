package com.ailab.storage.upload;

import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class TransientUploadTicketStore implements UploadTicketStore {
    private final Map<String, UploadTicketEntity> tickets = new LinkedHashMap<>();

    @Override
    public synchronized UploadTicketEntity save(UploadTicketEntity ticket) {
        tickets.put(ticket.getId(), ticket);
        return ticket;
    }

    @Override
    public synchronized Optional<UploadTicketEntity> findByTokenHash(String tokenHash) {
        return tickets.values().stream()
                .filter(ticket -> Objects.equals(ticket.getTokenHash(), tokenHash))
                .findFirst();
    }

    @Override
    public synchronized int claimIssued(String id, Instant now) {
        UploadTicketEntity ticket = tickets.get(id);
        if (ticket == null || ticket.getStatus() != UploadTicketStatus.ISSUED || !ticket.getExpiresAt().isAfter(now)) {
            return 0;
        }
        ticket.setStatus(UploadTicketStatus.UPLOADING);
        return 1;
    }

    @Override
    public synchronized Optional<UploadTicketEntity> findLatest(String assetId, String actorId, UploadScope scope,
                                                                String workspaceId, String previewId, String variant) {
        return tickets.values().stream()
                .filter(ticket -> Objects.equals(ticket.getAssetId(), assetId))
                .filter(ticket -> Objects.equals(ticket.getActorId(), actorId))
                .filter(ticket -> ticket.getScope() == scope)
                .filter(ticket -> workspaceId == null || Objects.equals(ticket.getWorkspaceId(), workspaceId))
                .filter(ticket -> previewId == null || Objects.equals(ticket.getPreviewId(), previewId))
                .filter(ticket -> variant == null || Objects.equals(ticket.getVariant(), variant))
                .max(Comparator.comparing(UploadTicketEntity::getId));
    }

    @Override
    public synchronized Optional<UploadTicketEntity> findLatestByAssetId(String assetId) {
        return tickets.values().stream()
                .filter(ticket -> Objects.equals(ticket.getAssetId(), assetId))
                .max(Comparator.comparing(UploadTicketEntity::getId));
    }
}
