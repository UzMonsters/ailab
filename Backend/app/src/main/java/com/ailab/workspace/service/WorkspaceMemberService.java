package com.ailab.workspace.service;

import com.ailab.user.domain.User;
import com.ailab.user.repository.UserRepository;
import com.ailab.workspace.domain.WorkspaceEntity;
import com.ailab.workspace.domain.WorkspaceInvitationEntity;
import com.ailab.workspace.domain.WorkspaceMemberEntity;
import com.ailab.workspace.domain.WorkspaceMemberId;
import com.ailab.workspace.dto.*;
import com.ailab.workspace.repository.WorkspaceInvitationRepository;
import com.ailab.workspace.repository.WorkspaceMemberRepository;
import com.ailab.workspace.repository.WorkspaceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;

@Service
public class WorkspaceMemberService {

    private final WorkspaceMemberRepository memberRepository;
    private final WorkspaceInvitationRepository invitationRepository;
    private final WorkspaceRepository workspaceRepository;
    private final UserRepository userRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public WorkspaceMemberService(
            WorkspaceMemberRepository memberRepository,
            WorkspaceInvitationRepository invitationRepository,
            WorkspaceRepository workspaceRepository,
            UserRepository userRepository
    ) {
        this.memberRepository = memberRepository;
        this.invitationRepository = invitationRepository;
        this.workspaceRepository = workspaceRepository;
        this.userRepository = userRepository;
    }

    public List<WorkspaceMemberDto> listMembers(String workspaceId, String actorUserId) {
        requirePermission(workspaceId, actorUserId, "READ_WORKSPACE");
        List<WorkspaceMemberEntity> members = memberRepository.findByWorkspaceId(workspaceId);
        List<WorkspaceMemberDto> dtos = new ArrayList<>();

        for (WorkspaceMemberEntity m : members) {
            String displayName = "User " + m.getUserId().substring(0, Math.min(6, m.getUserId().length()));
            String emailMasked = "";
            String avatarUrl = null;

            Optional<User> userOpt = userRepository.findById(m.getUserId());
            if (userOpt.isPresent()) {
                User u = userOpt.get();
                displayName = u.getUsername();
                emailMasked = WorkspaceMemberDto.maskEmail(u.getEmail());
                avatarUrl = u.getAvatarUrl();
            }

            dtos.add(new WorkspaceMemberDto(
                    m.getUserId(),
                    displayName,
                    emailMasked,
                    avatarUrl,
                    m.getRole(),
                    "ACTIVE",
                    m.getJoinedAt(),
                    m.getUpdatedAt()
            ));
        }

        return dtos;
    }

    public WorkspacePermissionsDto getPermissions(String workspaceId, String userId) {
        WorkspaceEntity ws = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workspace not found: " + workspaceId));

        if (ws.getOwnerId().equals(userId)) {
            return WorkspacePermissionsDto.of("OWNER", getOwnerCapabilities());
        }

        Optional<WorkspaceMemberEntity> memberOpt = memberRepository.findByWorkspaceIdAndUserId(workspaceId, userId);
        if (memberOpt.isPresent()) {
            String role = memberOpt.get().getRole().toUpperCase();
            return WorkspacePermissionsDto.of(role, getCapabilitiesForRole(role));
        }

        return WorkspacePermissionsDto.of("NONE", List.of());
    }

    @Transactional
    public WorkspaceMemberDto updateMemberRole(String workspaceId, String actorUserId, String targetUserId, String newRole) {
        requirePermission(workspaceId, actorUserId, "MANAGE_ACCESS");
        WorkspaceMemberEntity member = memberRepository.findByWorkspaceIdAndUserId(workspaceId, targetUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found: " + targetUserId));

        if ("OWNER".equalsIgnoreCase(member.getRole()) && !"OWNER".equalsIgnoreCase(newRole)) {
            long ownerCount = memberRepository.countOwnersByWorkspaceId(workspaceId);
            if (ownerCount <= 1) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "LAST_OWNER: Cannot demote the last remaining owner");
            }
        }

        member.setRole(newRole.toUpperCase());
        member.setUpdatedAt(Instant.now());
        memberRepository.save(member);

        return new WorkspaceMemberDto(
                targetUserId,
                "User " + targetUserId,
                "",
                null,
                member.getRole(),
                "ACTIVE",
                member.getJoinedAt(),
                member.getUpdatedAt()
        );
    }

    @Transactional
    public void removeMember(String workspaceId, String actorUserId, String targetUserId) {
        requirePermission(workspaceId, actorUserId, "MANAGE_ACCESS");
        WorkspaceMemberEntity member = memberRepository.findByWorkspaceIdAndUserId(workspaceId, targetUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found: " + targetUserId));

        if ("OWNER".equalsIgnoreCase(member.getRole())) {
            long ownerCount = memberRepository.countOwnersByWorkspaceId(workspaceId);
            if (ownerCount <= 1) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "LAST_OWNER: Cannot remove the last remaining owner");
            }
        }

        memberRepository.deleteByWorkspaceIdAndUserId(workspaceId, targetUserId);
    }

    @Transactional
    public Map<String, Object> createInvitation(String workspaceId, String actorUserId, CreateInvitationRequest request) {
        requirePermission(workspaceId, actorUserId, "MANAGE_ACCESS");

        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        String tokenHash = sha256(rawToken);

        String id = "inv_" + UUID.randomUUID().toString().substring(0, 12);
        Instant expiresAt = request.expiresAt() != null ? request.expiresAt() : Instant.now().plusSeconds(7 * 24 * 3600);

        WorkspaceInvitationEntity inv = new WorkspaceInvitationEntity(
                id, workspaceId, actorUserId, request.emailOrUserId(),
                request.role() != null ? request.role().toUpperCase() : "EDITOR",
                tokenHash, expiresAt
        );
        invitationRepository.save(inv);

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("invitationId", id);
        resp.put("workspaceId", workspaceId);
        resp.put("role", inv.getRole());
        resp.put("rawToken", rawToken); // returned only once
        resp.put("acceptUrl", "/workspace-invitations/" + rawToken + "/accept");
        resp.put("expiresAt", expiresAt.toString());
        return resp;
    }

    public List<WorkspaceInvitationDto> listInvitations(String workspaceId, String actorUserId) {
        requirePermission(workspaceId, actorUserId, "MANAGE_ACCESS");
        List<WorkspaceInvitationEntity> list = invitationRepository.findByWorkspaceId(workspaceId);
        return list.stream().map(i -> new WorkspaceInvitationDto(
                i.getId(),
                i.getWorkspaceId(),
                Map.of("email", i.getEmail()),
                i.getRole(),
                i.getStatus(),
                i.getExpiresAt(),
                i.getCreatedAt()
        )).toList();
    }

    @Transactional
    public void revokeInvitation(String workspaceId, String actorUserId, String invitationId) {
        requirePermission(workspaceId, actorUserId, "MANAGE_ACCESS");
        WorkspaceInvitationEntity inv = invitationRepository.findByIdAndWorkspaceId(invitationId, workspaceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invitation not found: " + invitationId));
        inv.setStatus("REVOKED");
        invitationRepository.save(inv);
    }

    @Transactional
    public Map<String, Object> acceptInvitation(String token, String authenticatedUserId) {
        String tokenHash = sha256(token);
        WorkspaceInvitationEntity inv = invitationRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid invitation token"));

        if (!"PENDING".equalsIgnoreCase(inv.getStatus())) {
            throw new ResponseStatusException(HttpStatus.GONE, "Invitation is no longer pending: " + inv.getStatus());
        }
        if (inv.getExpiresAt() != null && inv.getExpiresAt().isBefore(Instant.now())) {
            inv.setStatus("EXPIRED");
            invitationRepository.save(inv);
            throw new ResponseStatusException(HttpStatus.GONE, "Invitation has expired");
        }

        // Add member
        Optional<WorkspaceMemberEntity> existing = memberRepository.findByWorkspaceIdAndUserId(inv.getWorkspaceId(), authenticatedUserId);
        if (existing.isEmpty()) {
            WorkspaceMemberEntity member = new WorkspaceMemberEntity(inv.getWorkspaceId(), authenticatedUserId, inv.getRole());
            memberRepository.save(member);
        }

        inv.setStatus("ACCEPTED");
        invitationRepository.save(inv);

        return Map.of(
                "accepted", true,
                "workspaceId", inv.getWorkspaceId(),
                "role", inv.getRole()
        );
    }

    public void requirePermission(String workspaceId, String userId, String requiredCapability) {
        WorkspacePermissionsDto perms = getPermissions(workspaceId, userId);
        if ("NONE".equals(perms.role())) {
            throw new com.ailab.workspace.exception.WorkspaceNotFoundException(workspaceId);
        }
        if (!perms.capabilities().contains(requiredCapability)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Permission denied. Required capability: " + requiredCapability);
        }
    }

    public static List<String> getOwnerCapabilities() {
        return List.of(
                "READ_WORKSPACE", "EDIT_SCENE", "RUN_EXPERIMENT", "USE_MEASUREMENTS",
                "CHAT", "COMMENT", "MANAGE_ACCESS", "MANAGE_WORKSPACE"
        );
    }

    public static List<String> getCapabilitiesForRole(String role) {
        if ("OWNER".equalsIgnoreCase(role)) {
            return getOwnerCapabilities();
        }
        if ("EDITOR".equalsIgnoreCase(role)) {
            return List.of(
                    "READ_WORKSPACE", "EDIT_SCENE", "RUN_EXPERIMENT", "USE_MEASUREMENTS",
                    "CHAT", "COMMENT"
            );
        }
        if ("VIEWER".equalsIgnoreCase(role)) {
            return List.of(
                    "READ_WORKSPACE", "USE_MEASUREMENTS", "CHAT", "COMMENT"
            );
        }
        return List.of();
    }

    public static String sha256(String raw) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
