package com.ailab.workspace.security;

import com.ailab.workspace.domain.WorkspaceShareLinkEntity;
import com.ailab.workspace.repository.WorkspaceShareLinkRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.*;

@Service
public class WorkspaceShareSessionService {

    public static final String AUTH_NAME_PREFIX = "share_session:";

    private final WorkspaceShareLinkRepository shareLinkRepository;
    private final ObjectMapper objectMapper;
    private final byte[] secret;
    private final long sessionTtlSeconds;

    public WorkspaceShareSessionService(
            WorkspaceShareLinkRepository shareLinkRepository,
            ObjectMapper objectMapper,
            @Value("${app.share-session.secret:local-dev-share-session-secret-must-be-at-least-256-bits-long-32-bytes}") String secret,
            @Value("${app.share-session.ttl-seconds:43200}") long sessionTtlSeconds
    ) {
        if (secret == null || secret.isBlank() || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("Share session secret must be configured and at least 32 bytes (256 bits) long.");
        }
        String activeProfile = System.getProperty("spring.profiles.active", System.getenv().getOrDefault("SPRING_PROFILES_ACTIVE", ""));
        if ("production".equalsIgnoreCase(activeProfile) && (secret.contains("change-me") || secret.contains("local-dev"))) {
            throw new IllegalStateException("Default share session secret cannot be used in production.");
        }
        this.shareLinkRepository = shareLinkRepository;
        this.objectMapper = objectMapper;
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        this.sessionTtlSeconds = Math.max(60, sessionTtlSeconds);
    }

    public String issue(WorkspaceShareLinkEntity link) {
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("sid", UUID.randomUUID().toString());
            payload.put("linkId", link.getId());
            payload.put("workspaceId", link.getWorkspaceId());
            payload.put("role", normalizedRole(link.getRole()));
            payload.put("capabilities", capabilitiesForShareLink(link));
            Instant now = Instant.now();
            Instant sessionExpiresAt = now.plusSeconds(sessionTtlSeconds);
            if (link.getExpiresAt() != null && link.getExpiresAt().isBefore(sessionExpiresAt)) {
                sessionExpiresAt = link.getExpiresAt();
            }
            payload.put("iat", now.getEpochSecond());
            payload.put("exp", sessionExpiresAt.getEpochSecond());
            String encodedPayload = base64Url(objectMapper.writeValueAsBytes(payload));
            return "guest_sess_" + encodedPayload + "." + sign(encodedPayload);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to issue share session token", e);
        }
    }

    public ShareSessionPrincipal validate(String token, String expectedWorkspaceId) {
        if (token == null || !token.startsWith("guest_sess_")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "SHARE_SESSION_INVALID: Share session token is required");
        }
        try {
            String signed = token.substring("guest_sess_".length());
            String[] parts = signed.split("\\.", 2);
            if (parts.length != 2 || !constantEquals(sign(parts[0]), parts[1])) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "SHARE_SESSION_INVALID: Invalid share session signature");
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> payload = objectMapper.readValue(Base64.getUrlDecoder().decode(parts[0]), Map.class);
            String linkId = string(payload.get("linkId"));
            String workspaceId = string(payload.get("workspaceId"));
            if (expectedWorkspaceId != null && !expectedWorkspaceId.equals(workspaceId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "CAPABILITY_REQUIRED: Share session does not grant this workspace");
            }

            WorkspaceShareLinkEntity link = shareLinkRepository.findByIdAndWorkspaceId(linkId, workspaceId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.GONE, "SHARE_LINK_REVOKED: Share link is no longer valid"));
            assertLinkUsable(link);

            Instant expiresAt = null;
            if (payload.get("exp") instanceof Number exp) {
                expiresAt = Instant.ofEpochSecond(exp.longValue());
                if (expiresAt.isBefore(Instant.now())) {
                    throw new ResponseStatusException(HttpStatus.GONE, "SHARE_LINK_EXPIRED: Share session expired");
                }
            }

            List<String> capabilities = capabilitiesForShareLink(link);
            String guestId = "guest_" + Integer.toHexString(Objects.hash(linkId, workspaceId, string(payload.get("sid"))));
            return new ShareSessionPrincipal(guestId, linkId, workspaceId, normalizedRole(link.getRole()), capabilities, expiresAt);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "SHARE_SESSION_INVALID: Invalid share session token");
        }
    }

    public Authentication authenticationFor(ShareSessionPrincipal principal) {
        List<SimpleGrantedAuthority> authorities = principal.capabilities().stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                principal.guestId(), null, authorities);
        auth.setDetails(principal);
        return auth;
    }

    public Optional<ShareSessionPrincipal> currentSharePrincipal(String userId) {
        Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !Objects.equals(auth.getName(), userId)) {
            return Optional.empty();
        }
        Object details = auth.getDetails();
        if (details instanceof ShareSessionPrincipal principal) {
            return Optional.of(principal);
        }
        return Optional.empty();
    }

    public void assertLinkUsable(WorkspaceShareLinkEntity link) {
        if (link.getRevokedAt() != null) {
            throw new ResponseStatusException(HttpStatus.GONE, "SHARE_LINK_REVOKED: This share link has been revoked");
        }
        if (link.getExpiresAt() != null && link.getExpiresAt().isBefore(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.GONE, "SHARE_LINK_EXPIRED: This share link has expired");
        }
        if (link.getMaxUses() != null && link.getUseCount() >= link.getMaxUses()) {
            throw new ResponseStatusException(HttpStatus.GONE, "SHARE_LINK_LIMIT_REACHED: Maximum uses exceeded");
        }
    }

    public static List<String> capabilitiesForRole(String role) {
        String normalized = normalizedRole(role);
        List<String> caps = new ArrayList<>();
        caps.add("READ_WORKSPACE");
        caps.add("USE_MEASUREMENTS");
        caps.add("workspace.read");
        if ("EDITOR".equals(normalized) || "OWNER".equals(normalized)) {
            caps.add("EDIT_SCENE");
            caps.add("RUN_EXPERIMENT");
            caps.add("workspace.edit");
        }
        return caps;
    }

    public static List<String> ownerCapabilities() {
        List<String> caps = new ArrayList<>(capabilitiesForRole("OWNER"));
        caps.addAll(List.of("CHAT", "COMMENT", "MANAGE_ACCESS", "MANAGE_WORKSPACE",
                "workspace.chat", "workspace.comment", "workspace.manage_links", "workspace.manage_members"));
        return List.copyOf(new LinkedHashSet<>(caps));
    }

    public static List<String> memberCapabilities(String role) {
        String normalized = normalizedRole(role);
        List<String> caps = new ArrayList<>(capabilitiesForRole(normalized));
        if ("EDITOR".equals(normalized) || "OWNER".equals(normalized) || "VIEWER".equals(normalized)) {
            caps.add("CHAT");
            caps.add("COMMENT");
            caps.add("workspace.chat");
            caps.add("workspace.comment");
        }
        if ("OWNER".equals(normalized)) {
            caps.addAll(ownerCapabilities());
        }
        return List.copyOf(new LinkedHashSet<>(caps));
    }

    public static List<String> capabilitiesForShareLink(WorkspaceShareLinkEntity link) {
        List<String> caps = new ArrayList<>(capabilitiesForRole(link.getRole()));
        if (link.isAllowChat()) {
            caps.add("CHAT");
            caps.add("workspace.chat");
        }
        if (link.isAllowComments()) {
            caps.add("COMMENT");
            caps.add("workspace.comment");
        }
        return List.copyOf(new LinkedHashSet<>(caps));
    }

    public static String statusOf(WorkspaceShareLinkEntity link) {
        if (link.getRevokedAt() != null) return "REVOKED";
        if (link.getExpiresAt() != null && link.getExpiresAt().isBefore(Instant.now())) return "EXPIRED";
        if (link.getMaxUses() != null && link.getUseCount() >= link.getMaxUses()) return "LIMIT_REACHED";
        return "ACTIVE";
    }

    private String sign(String encodedPayload) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret, "HmacSHA256"));
            return base64Url(mac.doFinal(encodedPayload.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("HMAC-SHA256 is not available", e);
        }
    }

    private static String base64Url(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static boolean constantEquals(String expected, String actual) {
        return MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8), actual.getBytes(StandardCharsets.UTF_8));
    }

    private static String normalizedRole(String role) {
        return role == null || role.isBlank() ? "VIEWER" : role.trim().toUpperCase(Locale.ROOT);
    }

    private static String string(Object value) {
        return value == null ? "" : value.toString();
    }
}
