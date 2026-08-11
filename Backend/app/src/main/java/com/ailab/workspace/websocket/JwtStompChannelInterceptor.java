package com.ailab.workspace.websocket;

import com.ailab.auth.security.JwtService;
import com.ailab.user.domain.User;
import com.ailab.user.repository.UserRepository;
import com.ailab.workspace.repository.WorkspaceRepository;
import io.jsonwebtoken.Claims;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.List;

@Component
public class JwtStompChannelInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;

    public JwtStompChannelInterceptor(
            JwtService jwtService,
            UserRepository userRepository,
            WorkspaceRepository workspaceRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.workspaceRepository = workspaceRepository;
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
                Claims claims = jwtService.parse(token);
                String userId = claims.getSubject();
                String role = claims.get("role", String.class);
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new AuthenticationCredentialsNotFoundException("Token subject is not an active user"));
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        user.getId(), null, List.of(new SimpleGrantedAuthority(role != null ? role : "ROLE_USER")));
                accessor.setUser(auth);
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
                if (workspaceId != null && workspaceRepository.findByIdAndOwnerId(workspaceId, userId).isEmpty()) {
                    throw new org.springframework.security.access.AccessDeniedException("Workspace access denied: " + workspaceId);
                }
                String sessionId = extractScopedId(destination, "experiments");
                if (sessionId != null) {
                    var workspace = workspaceRepository.findByExperimentSessionId(sessionId);
                    if (workspace.isPresent() && !workspace.get().getOwnerId().equals(userId)) {
                        throw new org.springframework.security.access.AccessDeniedException("Experiment access denied: " + sessionId);
                    }
                }
            }
        }
        return message;
    }

    private String extractScopedId(String destination, String scope) {
        // Examples: /topic/workspaces/ws_123, /app/experiments/exp_123/commands
        String[] parts = destination.split("/");
        for (int i = 0; i < parts.length - 1; i++) {
            if (scope.equals(parts[i])) {
                return parts[i + 1];
            }
        }
        return null;
    }
}
