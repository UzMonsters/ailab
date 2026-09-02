package com.ailab.workspace.service;

import com.ailab.workspace.domain.WorkspaceEntity;
import com.ailab.workspace.domain.WorkspaceShareLinkEntity;
import com.ailab.workspace.dto.*;
import com.ailab.workspace.repository.WorkspaceRepository;
import com.ailab.workspace.repository.WorkspaceShareLinkRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;

@Service
public class WorkspaceShareService {

    private final WorkspaceShareLinkRepository shareLinkRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    public WorkspaceShareService(
            WorkspaceShareLinkRepository shareLinkRepository,
            WorkspaceRepository workspaceRepository,
            WorkspaceMemberService memberService,
            PasswordEncoder passwordEncoder
    ) {
        this.shareLinkRepository = shareLinkRepository;
        this.workspaceRepository = workspaceRepository;
        this.memberService = memberService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Map<String, Object> createShareLink(String workspaceId, String actorUserId, CreateShareLinkRequest request) {
        memberService.requirePermission(workspaceId, actorUserId, "MANAGE_ACCESS");

        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        String tokenHash = WorkspaceMemberService.sha256(rawToken);

        String id = "link_" + UUID.randomUUID().toString().substring(0, 12);
        String passwordHash = (request.password() != null && !request.password().isBlank())
                ? passwordEncoder.encode(request.password())
                : null;

        WorkspaceShareLinkEntity link = new WorkspaceShareLinkEntity(
                id, workspaceId, tokenHash,
                request.role() != null ? request.role().toUpperCase() : "VIEWER",
                passwordHash,
                request.maxUses(),
                request.allowChat(),
                request.allowComments(),
                request.expiresAt()
        );
        shareLinkRepository.save(link);

        String shareUrl = "/shared-workspaces/" + rawToken;
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("id", id);
        resp.put("linkId", id);
        resp.put("token", rawToken); // returned only once
        resp.put("url", shareUrl);
        resp.put("shareUrl", shareUrl);
        resp.put("role", link.getRole());
        resp.put("hasPassword", passwordHash != null);
        resp.put("expiresAt", link.getExpiresAt());
        resp.put("maxUses", link.getMaxUses());
        resp.put("allowChat", link.isAllowChat());
        resp.put("allowComments", link.isAllowComments());
        resp.put("capabilities", getShareLinkCapabilities(link));
        resp.put("createdAt", link.getCreatedAt());
        return resp;
    }

    public List<WorkspaceShareLinkDto> listShareLinks(String workspaceId, String actorUserId) {
        memberService.requirePermission(workspaceId, actorUserId, "MANAGE_ACCESS");
        List<WorkspaceShareLinkEntity> list = shareLinkRepository.findByWorkspaceId(workspaceId);
        return list.stream().map(l -> new WorkspaceShareLinkDto(
                l.getId(),
                "/shared-workspaces/hidden-token",
                l.getRole(),
                l.getExpiresAt(),
                l.getMaxUses(),
                l.getUseCount(),
                l.isAllowChat(),
                l.isAllowComments(),
                getShareLinkCapabilities(l),
                l.getLastUsedAt(),
                l.getCreatedAt()
        )).toList();
    }

    @Transactional
    public WorkspaceShareLinkDto updateShareLink(String workspaceId, String actorUserId, String linkId, UpdateShareLinkRequest request) {
        memberService.requirePermission(workspaceId, actorUserId, "MANAGE_ACCESS");
        WorkspaceShareLinkEntity link = shareLinkRepository.findByIdAndWorkspaceId(linkId, workspaceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Share link not found: " + linkId));

        if (request.role() != null) link.setRole(request.role().toUpperCase());
        if (request.expiresAt() != null) link.setExpiresAt(request.expiresAt());
        if (request.maxUses() != null) link.setMaxUses(request.maxUses());
        if (request.allowChat() != null) link.setAllowChat(request.allowChat());
        if (request.allowComments() != null) link.setAllowComments(request.allowComments());

        shareLinkRepository.save(link);

        return new WorkspaceShareLinkDto(
                link.getId(),
                "/shared-workspaces/hidden-token",
                link.getRole(),
                link.getExpiresAt(),
                link.getMaxUses(),
                link.getUseCount(),
                link.isAllowChat(),
                link.isAllowComments(),
                getShareLinkCapabilities(link),
                link.getLastUsedAt(),
                link.getCreatedAt()
        );
    }

    @Transactional
    public void revokeShareLink(String workspaceId, String actorUserId, String linkId) {
        memberService.requirePermission(workspaceId, actorUserId, "MANAGE_ACCESS");
        WorkspaceShareLinkEntity link = shareLinkRepository.findByIdAndWorkspaceId(linkId, workspaceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Share link not found: " + linkId));
        link.setRevokedAt(Instant.now());
        shareLinkRepository.save(link);
    }

    @Transactional
    public ResolveShareLinkResponse resolveShareLink(ResolveShareLinkRequest request) {
        String tokenHash = WorkspaceMemberService.sha256(request.token());
        WorkspaceShareLinkEntity link = shareLinkRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SHARE_LINK_NOT_FOUND: Invalid share token"));

        if (link.getRevokedAt() != null) {
            throw new ResponseStatusException(HttpStatus.GONE, "SHARE_LINK_EXPIRED_OR_REVOKED: This share link has been revoked");
        }
        if (link.getExpiresAt() != null && link.getExpiresAt().isBefore(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.GONE, "SHARE_LINK_EXPIRED_OR_REVOKED: This share link has expired");
        }
        if (link.getMaxUses() != null && link.getUseCount() >= link.getMaxUses()) {
            throw new ResponseStatusException(HttpStatus.GONE, "SHARE_LINK_EXPIRED_OR_REVOKED: Maximum uses exceeded");
        }

        if (link.getPasswordHash() != null) {
            if (request.password() == null || request.password().isBlank()) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "SHARE_PASSWORD_REQUIRED: Password required to access this share link");
            }
            if (!passwordEncoder.matches(request.password(), link.getPasswordHash())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "SHARE_PASSWORD_INVALID: Incorrect password");
            }
        }

        // Increment use count
        link.setUseCount(link.getUseCount() + 1);
        link.setLastUsedAt(Instant.now());
        shareLinkRepository.save(link);

        WorkspaceEntity ws = workspaceRepository.findById(link.getWorkspaceId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workspace not found"));

        List<String> capabilities = getShareLinkCapabilities(link);
        String sessionToken = "guest_sess_" + Base64.getUrlEncoder().withoutPadding().encodeToString(
                (link.getWorkspaceId() + ":" + link.getRole() + ":" + System.currentTimeMillis()).getBytes()
        );

        return new ResolveShareLinkResponse(
                ws.getId(),
                ws.getName(),
                ws.getScience(),
                WorkspacePreviewDto.fallback("chemistry-default-01"),
                link.getRole(),
                capabilities,
                false,
                link.getExpiresAt(),
                sessionToken
        );
    }

    public List<String> getShareLinkCapabilities(WorkspaceShareLinkEntity link) {
        List<String> caps = new ArrayList<>();
        caps.add("READ_WORKSPACE");
        caps.add("USE_MEASUREMENTS");
        if ("EDITOR".equalsIgnoreCase(link.getRole())) {
            caps.add("EDIT_SCENE");
            caps.add("RUN_EXPERIMENT");
        }
        if (link.isAllowChat()) caps.add("CHAT");
        if (link.isAllowComments()) caps.add("COMMENT");
        return caps;
    }
}
