package com.ailab.admin.workspace;

import com.ailab.workspace.dto.WorkspaceInvitationDto;
import com.ailab.workspace.dto.WorkspaceMemberDto;
import com.ailab.workspace.dto.WorkspaceShareLinkDto;

import java.time.Instant;
import java.util.List;

public final class AdminWorkspaceDtos {
    private AdminWorkspaceDtos() {}

    public record OwnerDto(String id, String displayName, String emailMasked) {}

    public record SummaryDto(
            String id,
            String name,
            String science,
            String status,
            OwnerDto owner,
            long memberCount,
            long activeShareLinkCount,
            long pendingInvitationCount,
            long stateVersion,
            Instant updatedAt
    ) {}

    public record PageDto(
            List<SummaryDto> items,
            PageMeta page
    ) {}

    public record PageMeta(int page, int size, long totalElements, int totalPages) {}

    public record DetailDto(
            String id,
            String name,
            String science,
            String status,
            OwnerDto owner,
            long stateVersion,
            Instant createdAt,
            Instant updatedAt,
            List<WorkspaceMemberDto> members,
            List<WorkspaceInvitationDto> invitations,
            List<WorkspaceShareLinkDto> shareLinks,
            int eventCount
    ) {}
}
