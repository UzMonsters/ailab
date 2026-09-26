package com.ailab.workspace.websocket;

import com.ailab.auth.security.JwtService;
import com.ailab.user.domain.User;
import com.ailab.user.repository.UserRepository;
import com.ailab.workspace.dto.WorkspacePermissionsDto;
import com.ailab.workspace.repository.WorkspaceRepository;
import com.ailab.workspace.security.ShareSessionPrincipal;
import com.ailab.workspace.security.WorkspaceShareSessionService;
import com.ailab.workspace.service.WorkspaceMemberService;
import io.jsonwebtoken.Claims;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JwtStompChannelInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberService memberService;
    private final WorkspaceShareSessionService shareSessionService;

    // Active session tracking: workspaceId -> Set<sessionId>
    private final Map<String, Set<String>> workspaceActiveSessions = new ConcurrentHashMap<>();
    private final Map<String, String> sessionToUser = new ConcurrentHashMap<>();
    private final Map<String, String> sessionToShareToken = new ConcurrentHashMap<>();

    public JwtStompChannelInterceptor(
            JwtService jwtService,
            UserRepository userRepository,
            WorkspaceRepository workspaceRepository,
            WorkspaceMemberService memberService,
            WorkspaceShareSessionService shareSessionService) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.workspaceRepository = workspaceRepository;
        this.memberService = memberService;
        this.shareSessionService = shareSessionService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) return message;

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("ShareSession ")) {
                authHeader = "Bearer " + authHeader.substring("ShareSession ".length()).trim();
            }
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                authHeader = accessor.getFirstNativeHeader("accessToken");
                if (authHeader != null && !authHeader.startsWith("Bearer ")) {
                    authHeader = "Bearer " + authHeader;
                }
            }

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new AuthenticationCredentialsNotFoundException("STOMP CONNECT requires Bearer token");
            }
            try {
                String token = authHeader.substring(7);
                if (token.startsWith("guest_sess_")) {
                    if (accessor.getSessionId() != null) {
                        sessionToShareToken.put(accessor.getSessionId(), token);
                    }
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            WorkspaceShareSessionService.AUTH_NAME_PREFIX + token, null, List.of(new SimpleGrantedAuthority("ROLE_GUEST")));
                    accessor.setUser(auth);
                } else {
                    Claims claims = jwtService.parse(token);
                    String userId = claims.getSubject();
                    String role = claims.get("role", String.class);
                    Number tokenVersion = claims.get("tokenVersion", Number.class);
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new AuthenticationCredentialsNotFoundException("Token subject is not an active user"));
                    if (tokenVersion == null || user.getTokenVersion() != tokenVersion.longValue()) {
                        throw new AuthenticationCredentialsNotFoundException("Token version is invalid or expired");
                    }
                    String status = user.getStatus();
                    if ("BLOCKED".equalsIgnoreCase(status) || "DEACTIVATED".equalsIgnoreCase(status) || "DELETION_SCHEDULED".equalsIgnoreCase(status)) {
                        throw new AuthenticationCredentialsNotFoundException("Account is blocked or deactivated");
                    }
                    if (user.getBlockedUntil() != null && user.getBlockedUntil().isAfter(java.time.Instant.now())) {
                        throw new AuthenticationCredentialsNotFoundException("Account is temporarily suspended");
                    }
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            user.getId(), null, List.of(new SimpleGrantedAuthority(role != null ? role : "ROLE_USER")));
                    accessor.setUser(auth);
                }

                if (accessor.getSessionId() != null && accessor.getUser() != null) {
                    sessionToUser.put(accessor.getSessionId(), accessor.getUser().getName());
                }
            } catch (AuthenticationCredentialsNotFoundException e) {
                throw e;
            } catch (Exception e) {
                throw new AuthenticationCredentialsNotFoundException("Invalid STOMP Bearer token", e);
            }
        } else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand()) || StompCommand.SEND.equals(accessor.getCommand())) {
            Principal user = accessor.getUser();
            String destination = accessor.getDestination();
            if (destination != null) {
                if (user == null) {
                    throw new AuthenticationCredentialsNotFoundException("STOMP message requires authenticated user");
                }
                String userId = user.getName();
                String workspaceId = extractScopedId(destination, "workspaces");
                if (workspaceId != null) {
                    ShareSessionPrincipal sharePrincipal = null;
                    if (isShareSessionUser(userId, accessor.getSessionId())) {
                        String token = shareTokenFor(userId, accessor.getSessionId());
                        sharePrincipal = shareSessionService.validate(token, workspaceId);
                        Authentication auth = shareSessionService.authenticationFor(sharePrincipal);
                        accessor.setUser(auth);
                        org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(auth);
                        userId = sharePrincipal.guestId();
                        if (accessor.getSessionId() != null) {
                            sessionToUser.put(accessor.getSessionId(), userId);
                        }
                    }

                    // Track session
                    if (accessor.getSessionId() != null) {
                        workspaceActiveSessions.computeIfAbsent(workspaceId, k -> ConcurrentHashMap.newKeySet()).add(accessor.getSessionId());
                    }

                    WorkspacePermissionsDto perms = sharePrincipal != null
                            ? WorkspacePermissionsDto.of(sharePrincipal.role(), sharePrincipal.capabilities())
                            : memberService.getPermissions(workspaceId, userId);
                    if ("NONE".equals(perms.role())) {
                        throw new AccessDeniedException("Workspace access denied: " + workspaceId);
                    }

                    // Capability validation
                    if (destination.contains("/chat")) {
                        if (!perms.capabilities().contains("CHAT")) {
                            throw new AccessDeniedException("Chat permission denied");
                        }
                    } else if (destination.contains("/comments")) {
                        if (!perms.capabilities().contains("COMMENT")) {
                            throw new AccessDeniedException("Comments permission denied");
                        }
                    } else if (StompCommand.SEND.equals(accessor.getCommand()) && destination.contains("/events")) {
                        if (!perms.capabilities().contains("EDIT_SCENE")) {
                            throw new AccessDeniedException("Edit scene permission denied for user: " + userId);
                        }
                    }
                }

                String sessionId = extractScopedId(destination, "experiments");
                if (sessionId != null) {
                    var workspace = workspaceRepository.findByExperimentSessionId(sessionId);
                    if (workspace.isPresent()) {
                        WorkspacePermissionsDto perms;
                        if (isShareSessionUser(userId, accessor.getSessionId())) {
                            String token = shareTokenFor(userId, accessor.getSessionId());
                            ShareSessionPrincipal principal = shareSessionService.validate(token, workspace.get().getId());
                            Authentication auth = shareSessionService.authenticationFor(principal);
                            accessor.setUser(auth);
                            org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(auth);
                            userId = principal.guestId();
                            if (accessor.getSessionId() != null) {
                                sessionToUser.put(accessor.getSessionId(), userId);
                            }
                            perms = WorkspacePermissionsDto.of(principal.role(), principal.capabilities());
                        } else {
                            perms = memberService.getPermissions(workspace.get().getId(), userId);
                        }
                        if ("NONE".equals(perms.role())) {
                            throw new AccessDeniedException("Experiment access denied: " + sessionId);
                        }
                        if (StompCommand.SEND.equals(accessor.getCommand()) && !perms.capabilities().contains("RUN_EXPERIMENT")) {
                            throw new AccessDeniedException("Experiment command permission denied");
                        }
                    }
                }
            }
        } else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            if (accessor.getSessionId() != null) {
                sessionToUser.remove(accessor.getSessionId());
                sessionToShareToken.remove(accessor.getSessionId());
                for (Set<String> sessions : workspaceActiveSessions.values()) {
                    sessions.remove(accessor.getSessionId());
                }
            }
        }
        return message;
    }

    private boolean isShareSessionUser(String userId, String sessionId) {
        return userId != null
                && (userId.startsWith(WorkspaceShareSessionService.AUTH_NAME_PREFIX)
                || (userId.startsWith("guest_") && sessionId != null && sessionToShareToken.containsKey(sessionId)));
    }

    private String shareTokenFor(String userId, String sessionId) {
        if (sessionId != null && sessionToShareToken.containsKey(sessionId)) {
            return sessionToShareToken.get(sessionId);
        }
        if (userId != null && userId.startsWith(WorkspaceShareSessionService.AUTH_NAME_PREFIX)) {
            return userId.substring(WorkspaceShareSessionService.AUTH_NAME_PREFIX.length());
        }
        throw new AuthenticationCredentialsNotFoundException("Share session token is required");
    }

    public void revokeUserSessions(String workspaceId, String userId) {
        Set<String> sessions = workspaceActiveSessions.get(workspaceId);
        if (sessions != null) {
            sessions.removeIf(sessId -> {
                String u = sessionToUser.get(sessId);
                return userId.equals(u);
            });
        }
    }

    private String extractScopedId(String destination, String scope) {
        String[] parts = destination.split("/");
        for (int i = 0; i < parts.length - 1; i++) {
            if (scope.equals(parts[i])) {
                return parts[i + 1];
            }
        }
        return null;
    }
}
