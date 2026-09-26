package com.ailab.storage.upload;

import java.time.Instant;
import java.util.Optional;

public interface UploadTicketStore {
    UploadTicketEntity save(UploadTicketEntity ticket);

    Optional<UploadTicketEntity> findByTokenHash(String tokenHash);

    int claimIssued(String id, Instant now);

    Optional<UploadTicketEntity> findLatest(String assetId, String actorId, UploadScope scope,
                                            String workspaceId, String previewId, String variant);

    Optional<UploadTicketEntity> findLatestByAssetId(String assetId);
}
