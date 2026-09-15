package com.ailab.admin.workspace;

import com.ailab.user.domain.User;
import com.ailab.user.repository.UserRepository;
import com.ailab.workspace.domain.WorkspaceEntity;
import com.ailab.workspace.domain.WorkspaceShareLinkEntity;
import com.ailab.workspace.dto.WorkspaceInvitationDto;
import com.ailab.workspace.dto.WorkspaceMemberDto;
import com.ailab.workspace.dto.WorkspaceShareLinkDto;
import com.ailab.workspace.exception.WorkspaceNotFoundException;
import com.ailab.workspace.repository.*;
import com.ailab.workspace.security.WorkspaceShareSessionService;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
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
        Instant now = Instant.now();
        Page<WorkspaceEntity> workspaces = workspaceRepository.findAll(
                adminWorkspaceSpecification(q, science, normalizedStatus(status), ownerId, hasActiveLinks, now),
                pageable
        );

        List<String> workspaceIds = workspaces.getContent().stream()
                .map(WorkspaceEntity::getId)
                .toList();
        Map<String, Long> memberCounts = workspaceIds.isEmpty()
                ? Map.of()
                : countMap(memberRepository.countByWorkspaceIds(workspaceIds));
        Map<String, Long> activeShareLinkCounts = workspaceIds.isEmpty()
                ? Map.of()
                : countMap(shareLinkRepository.countActiveByWorkspaceIds(workspaceIds, now));
        Map<String, Long> pendingInvitationCounts = workspaceIds.isEmpty()
                ? Map.of()
                : countMap(invitationRepository.countPendingByWorkspaceIds(workspaceIds));

        Map<String, User> owners = userRepository.findAllById(workspaces.getContent().stream()
                        .map(WorkspaceEntity::getOwnerId)
                        .collect(Collectors.toSet()))
                .stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        List<AdminWorkspaceDtos.SummaryDto> items = workspaces.getContent().stream()
                .map(workspace -> new AdminWorkspaceDtos.SummaryDto(
                        workspace.getId(),
                        workspace.getName(),
                        workspace.getScience(),
                        workspace.isDeleted() ? "DELETED" : "ACTIVE",
                        owner(workspace.getOwnerId(), owners.get(workspace.getOwnerId())),
                        memberCounts.getOrDefault(workspace.getId(), 0L),
                        activeShareLinkCounts.getOrDefault(workspace.getId(), 0L),
                        pendingInvitationCounts.getOrDefault(workspace.getId(), 0L),
                        workspace.getStateVersion(),
                        workspace.getUpdatedAt()
                ))
                .toList();

        return new AdminWorkspaceDtos.PageDto(items,
                new AdminWorkspaceDtos.PageMeta(workspaces.getNumber(), workspaces.getSize(), workspaces.getTotalElements(), workspaces.getTotalPages()));
    }

    private Specification<WorkspaceEntity> adminWorkspaceSpecification(
            String q,
            String science,
            String status,
            String ownerId,
            Boolean hasActiveLinks,
            Instant now
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (hasText(science)) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("science")), normalized(science)));
            }
            if (hasText(q)) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")), "%" + normalized(q) + "%"));
            }
            if ("ACTIVE".equals(status)) {
                predicates.add(criteriaBuilder.isFalse(root.get("isDeleted")));
            } else if ("DELETED".equals(status)) {
                predicates.add(criteriaBuilder.isTrue(root.get("isDeleted")));
            }
            if (ownerId != null && !ownerId.isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("ownerId"), ownerId));
            }
            if (hasActiveLinks != null) {
                jakarta.persistence.criteria.Subquery<Integer> subquery = query.subquery(Integer.class);
                Root<WorkspaceShareLinkEntity> link = subquery.from(WorkspaceShareLinkEntity.class);
                List<Predicate> linkPredicates = new ArrayList<>();
                linkPredicates.add(criteriaBuilder.equal(link.get("workspaceId"), root.get("id")));
                linkPredicates.add(criteriaBuilder.isNull(link.get("revokedAt")));
                linkPredicates.add(criteriaBuilder.or(
                        criteriaBuilder.isNull(link.get("expiresAt")),
                        criteriaBuilder.greaterThan(link.get("expiresAt"), now)));
                linkPredicates.add(criteriaBuilder.or(
                        criteriaBuilder.isNull(link.get("maxUses")),
                        criteriaBuilder.lessThan(link.get("useCount"), link.get("maxUses"))));
                subquery.select(criteriaBuilder.literal(1));
                subquery.where(criteriaBuilder.and(linkPredicates.toArray(Predicate[]::new)));
                Predicate exists = criteriaBuilder.exists(subquery);
                predicates.add(Boolean.TRUE.equals(hasActiveLinks) ? exists : criteriaBuilder.not(exists));
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

    private Map<String, Long> countMap(List<Object[]> rows) {
        return rows.stream().collect(Collectors.toMap(
                row -> String.valueOf(row[0]),
                row -> ((Number) row[1]).longValue()
        ));
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
