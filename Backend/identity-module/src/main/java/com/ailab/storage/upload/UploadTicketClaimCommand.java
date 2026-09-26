package com.ailab.storage.upload;

public record UploadTicketClaimCommand(
        String token,
        String assetId,
        String actorId,
        UploadScope scope,
        String workspaceId,
        String previewId,
        String variant
) {
}
