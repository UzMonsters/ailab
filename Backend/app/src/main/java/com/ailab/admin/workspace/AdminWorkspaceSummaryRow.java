package com.ailab.admin.workspace;

import java.time.Instant;

public record AdminWorkspaceSummaryRow(
        String id,
        String name,
        String science,
        String status,
        String ownerId,
        long stateVersion,
        Instant updatedAt,
        long memberCount,
        long activeShareLinkCount,
        long pendingInvitationCount
) {}
