package com.ailab.workspace.websocket;

import com.ailab.auth.security.JwtService;
import com.ailab.user.domain.User;
import com.ailab.user.repository.UserRepository;
import com.ailab.workspace.dto.WorkspacePermissionsDto;
import com.ailab.workspace.repository.WorkspaceRepository;
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

    // Active session tracking: workspaceId -> Set<sessionId>
    private final Map<String, Set<String>> workspaceActiveSessions = new ConcurrentHashMap<>();
    private final Map<String, String> sessionToUser = new ConcurrentHashMap<>();

    public JwtStompChannelInterceptor(
            JwtService jwtService,
            UserRepository userRepository,
            WorkspaceRepository workspaceRepository,
            WorkspaceMemberService memberService) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.workspaceRepository = workspaceRepository;
        this.memberService = memberService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) return message;

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");
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
                    // Guest share session
                    String guestId = "guest_" + UUID.randomUUID().toString().substring(0, 8);
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            guestId, null, List.of(new SimpleGrantedAuthority("ROLE_GUEST")));
                    accessor.setUser(auth);
                } else {
                    Claims claims = jwtService.parse(token);
                    String userId = claims.getSubject();
                    String role = claims.get("role", String.class);
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new AuthenticationCredentialsNotFoundException("Token subject is not an active user"));
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
                    // Track session
                    if (accessor.getSessionId() != null) {
                        workspaceActiveSessions.computeIfAbsent(workspaceId, k -> ConcurrentHashMap.newKeySet()).add(accessor.getSessionId());
                    }

                    WorkspacePermissionsDto perms = memberService.getPermissions(workspaceId, userId);
                    if ("NONE".equals(perms.role()) && !userId.startsWith("guest_")) {
                        throw new AccessDeniedException("Workspace access denied: " + workspaceId);
                    }

                    // Capability validation
                    if (destination.contains("/chat")) {
                        if (!perms.capabilities().contains("CHAT") && !userId.startsWith("guest_")) {
                            throw new AccessDeniedException("Chat permission denied");
                        }
                    } else if (destination.contains("/comments")) {
                        if (!perms.capabilities().contains("COMMENT") && !userId.startsWith("guest_")) {
                            throw new AccessDeniedException("Comments permission denied");
                        }
                    } else if (StompCommand.SEND.equals(accessor.getCommand()) && destination.contains("/events")) {
                        if (!perms.capabilities().contains("EDIT_SCENE") && !userId.startsWith("guest_")) {
                            throw new AccessDeniedException("Edit scene permission denied for user: " + userId);
                        }
                    }
                }

                String sessionId = extractScopedId(destination, "experiments");
                if (sessionId != null) {
                    var workspace = workspaceRepository.findByExperimentSessionId(sessionId);
                    if (workspace.isPresent()) {
                        WorkspacePermissionsDto perms = memberService.getPermissions(workspace.get().getId(), userId);
                        if ("NONE".equals(perms.role()) && !userId.startsWith("guest_")) {
                            throw new AccessDeniedException("Experiment access denied: " + sessionId);
                        }
                    }
                }
            }
        } else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            if (accessor.getSessionId() != null) {
                sessionToUser.remove(accessor.getSessionId());
                for (Set<String> sessions : workspaceActiveSessions.values()) {
                    sessions.remove(accessor.getSessionId());
                }
            }
        }
        return message;
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
