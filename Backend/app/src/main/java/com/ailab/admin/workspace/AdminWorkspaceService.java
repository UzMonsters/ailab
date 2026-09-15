package com.ailab.admin.workspace;

import com.ailab.user.domain.User;
import com.ailab.user.repository.UserRepository;
import com.ailab.workspace.domain.WorkspaceEntity;
import com.ailab.workspace.dto.WorkspaceInvitationDto;
import com.ailab.workspace.dto.WorkspaceMemberDto;
import com.ailab.workspace.dto.WorkspaceShareLinkDto;
import com.ailab.workspace.exception.WorkspaceNotFoundException;
import com.ailab.workspace.repository.*;
import com.ailab.workspace.security.WorkspaceShareSessionService;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AdminWorkspaceService {
    private static final Set<String> SORT_FIELDS = Set.of("updatedAt", "createdAt", "name", "stateVersion");

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository memberRepository;
    private final WorkspaceInvitationRepository invitationRepository;
    private final WorkspaceShareLinkRepository shareLinkRepository;
    private final WorkspacePreviewRepository previewRepository;
    private final WorkspaceEventRepository eventRepository;
    private final WorkspaceStateRepository stateRepository;
    private final WorkspaceChatMessageRepository chatMessageRepository;
    private final WorkspaceChatReadRepository chatReadRepository;
    private final WorkspaceCommentThreadRepository commentThreadRepository;
    private final MeasurementRepository measurementRepository;
    private final UserRepository userRepository;

    public AdminWorkspaceService(
            WorkspaceRepository workspaceRepository,
            WorkspaceMemberRepository memberRepository,
            WorkspaceInvitationRepository invitationRepository,
            WorkspaceShareLinkRepository shareLinkRepository,
            WorkspacePreviewRepository previewRepository,
            WorkspaceEventRepository eventRepository,
            WorkspaceStateRepository stateRepository,
            WorkspaceChatMessageRepository chatMessageRepository,
            WorkspaceChatReadRepository chatReadRepository,
            WorkspaceCommentThreadRepository commentThreadRepository,
            MeasurementRepository measurementRepository,
            UserRepository userRepository
    ) {
        this.workspaceRepository = workspaceRepository;
        this.memberRepository = memberRepository;
        this.invitationRepository = invitationRepository;
        this.shareLinkRepository = shareLinkRepository;
        this.previewRepository = previewRepository;
        this.eventRepository = eventRepository;
        this.stateRepository = stateRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.chatReadRepository = chatReadRepository;
        this.commentThreadRepository = commentThreadRepository;
        this.measurementRepository = measurementRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public AdminWorkspaceDtos.PageDto list(String q, String science, String status, String ownerId,
                                           Boolean hasActiveLinks, int page, int size, String sort) {
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, Math.min(size, 100)), sort(sort));
        Page<WorkspaceEntity> pageResult = workspaceRepository.findAdminWorkspaces(
                normalized(q),
                hasText(q),
                normalized(science),
                hasText(science),
                normalizedStatus(status),
                ownerId != null && !ownerId.isBlank() ? ownerId : "",
                ownerId != null && !ownerId.isBlank(),
                hasActiveLinks,
                hasActiveLinks != null,
                Instant.now(),
                pageable
        );

        List<WorkspaceEntity> workspaces = pageResult.getContent();
        Set<String> wsIds = workspaces.stream().map(WorkspaceEntity::getId).collect(Collectors.toSet());

        Map<String, Long> memberCounts = wsIds.isEmpty() ? Collections.emptyMap() :
                memberRepository.countMembersByWorkspaceIds(wsIds).stream()
                        .collect(Collectors.toMap(r -> (String) r[0], r -> ((Number) r[1]).longValue()));

        Map<String, Long> linkCounts = wsIds.isEmpty() ? Collections.emptyMap() :
                shareLinkRepository.countActiveShareLinksByWorkspaceIds(wsIds, Instant.now()).stream()
                        .collect(Collectors.toMap(r -> (String) r[0], r -> ((Number) r[1]).longValue()));

        Map<String, Long> invitationCounts = wsIds.isEmpty() ? Collections.emptyMap() :
                invitationRepository.countPendingInvitationsByWorkspaceIds(wsIds).stream()
                        .collect(Collectors.toMap(r -> (String) r[0], r -> ((Number) r[1]).longValue()));

        Map<String, User> owners = userRepository.findAllById(workspaces.stream()
                        .map(WorkspaceEntity::getOwnerId)
                        .collect(Collectors.toSet()))
                .stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        List<AdminWorkspaceDtos.SummaryDto> items = workspaces.stream()
                .map(ws -> new AdminWorkspaceDtos.SummaryDto(
                        ws.getId(),
                        ws.getName(),
                        ws.getScience(),
                        ws.isDeleted() ? "DELETED" : "ACTIVE",
                        owner(ws.getOwnerId(), owners.get(ws.getOwnerId())),
                        memberCounts.getOrDefault(ws.getId(), 0L),
                        linkCounts.getOrDefault(ws.getId(), 0L),
                        invitationCounts.getOrDefault(ws.getId(), 0L),
                        ws.getStateVersion(),
                        ws.getUpdatedAt()
                ))
                .toList();

        return new AdminWorkspaceDtos.PageDto(items,
                new AdminWorkspaceDtos.PageMeta(pageResult.getNumber(), pageResult.getSize(), pageResult.getTotalElements(), pageResult.getTotalPages()));
    }

    @Transactional(readOnly = true)
    public AdminWorkspaceDtos.DetailDto detail(String id) {
        WorkspaceEntity workspace = workspaceRepository.findById(id)
                .orElseThrow(() -> new WorkspaceNotFoundException(id));
        User owner = userRepository.findById(workspace.getOwnerId()).orElse(null);

        List<WorkspaceMemberDto> members = memberRepository.findByWorkspaceId(id).stream()
                .map(m -> {
                    User user = userRepository.findById(m.getUserId()).orElse(null);
                    return new WorkspaceMemberDto(
                            m.getUserId(),
                            user != null ? displayName(user) : "User " + m.getUserId(),
                            user != null ? WorkspaceMemberDto.maskEmail(user.getEmail()) : "",
                            user != null ? user.getAvatarUrl() : null,
                            m.getRole(),
                            "ACTIVE",
                            m.getJoinedAt(),
                            m.getUpdatedAt()
                    );
                })
                .toList();
        List<WorkspaceInvitationDto> invitations = invitationRepository.findByWorkspaceId(id).stream()
                .map(i -> new WorkspaceInvitationDto(i.getId(), i.getWorkspaceId(),
                        Map.of("emailMasked", WorkspaceMemberDto.maskEmail(i.getEmail())),
                        i.getRole(), i.getStatus(), i.getExpiresAt(), i.getCreatedAt()))
                .toList();
        List<WorkspaceShareLinkDto> links = shareLinkRepository.findByWorkspaceId(id).stream()
                .map(l -> new WorkspaceShareLinkDto(l.getId(), "/shared-workspaces/hidden-token", l.getRole(),
                        l.getExpiresAt(), l.getMaxUses(), l.getUseCount(), l.isAllowChat(), l.isAllowComments(),
                        WorkspaceShareSessionService.capabilitiesForShareLink(l), l.getLastUsedAt(), l.getCreatedAt(),
                        WorkspaceShareSessionService.statusOf(l), l.getRevokedAt()))
                .toList();

        return new AdminWorkspaceDtos.DetailDto(
                workspace.getId(),
                workspace.getName(),
                workspace.getScience(),
                workspace.isDeleted() ? "DELETED" : "ACTIVE",
                owner(workspace.getOwnerId(), owner),
                workspace.getStateVersion(),
                workspace.getCreatedAt(),
                workspace.getUpdatedAt(),
                members,
                invitations,
                links,
                eventRepository.findByWorkspaceIdOrderByVersionAsc(id).size()
        );
    }

    @Transactional
    public void purge(String id) {
        WorkspaceEntity workspace = workspaceRepository.findById(id)
                .orElseThrow(() -> new WorkspaceNotFoundException(id));
        chatReadRepository.deleteByWorkspaceId(id);
        chatMessageRepository.deleteByWorkspaceId(id);
        commentThreadRepository.deleteByWorkspaceId(id);
        measurementRepository.deleteByWorkspaceId(id);
        eventRepository.deleteByWorkspaceId(id);
        previewRepository.deleteByWorkspaceId(id);
        shareLinkRepository.deleteByWorkspaceId(id);
        invitationRepository.deleteByWorkspaceId(id);
        memberRepository.deleteByWorkspaceId(id);
        stateRepository.deleteById(id);
        workspaceRepository.delete(workspace);
    }

    private Sort sort(String value) {
        if (value == null || value.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "updatedAt");
        }
        String[] parts = value.split(",");
        String field = parts[0].trim();
        if (!SORT_FIELDS.contains(field)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "INVALID_SORT: Unsupported workspace sort field: " + field);
        }
        Sort.Direction direction = parts.length > 1 && parts[1].trim().equalsIgnoreCase("asc")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        return Sort.by(direction, field);
    }

    private String normalized(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String normalizedStatus(String status) {
        if (status == null || status.isBlank()) return "";
        String normalized = status.trim().toUpperCase(Locale.ROOT);
        if (!Set.of("ACTIVE", "DELETED").contains(normalized)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "INVALID_STATUS: Unsupported workspace status: " + status);
        }
        return normalized;
    }

    private AdminWorkspaceDtos.OwnerDto owner(String ownerId, User user) {
        if (user == null) {
            return new AdminWorkspaceDtos.OwnerDto(ownerId, "User " + ownerId, "");
        }
        return new AdminWorkspaceDtos.OwnerDto(user.getId(), displayName(user), WorkspaceMemberDto.maskEmail(user.getEmail()));
    }

    private String displayName(User user) {
        return user.getDisplayName() != null && !user.getDisplayName().isBlank()
                ? user.getDisplayName()
                : user.getUsername();
    }
}
