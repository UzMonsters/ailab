package com.ailab.workspace.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class WorkspaceAccessResolver {

    private final WorkspaceShareSessionService shareSessionService;

    public WorkspaceAccessResolver(WorkspaceShareSessionService shareSessionService) {
        this.shareSessionService = shareSessionService;
    }

    public String requireUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || auth.getName().isBlank()
                || "anonymousUser".equalsIgnoreCase(auth.getName())
                || auth.getName().startsWith(WorkspaceShareSessionService.AUTH_NAME_PREFIX)
                || auth.getName().startsWith("guest_")) {
            throw new InsufficientAuthenticationException("User must be authenticated");
        }
        return auth.getName();
    }

    public String requireWorkspaceActor(String workspaceId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (isRealUser(auth)) {
            return auth.getName();
        }
        if (auth != null && auth.getDetails() instanceof ShareSessionPrincipal principal
                && workspaceId.equals(principal.workspaceId())) {
            return principal.guestId();
        }
        String token = shareSessionToken();
        ShareSessionPrincipal principal = shareSessionService.validate(token, workspaceId);
        SecurityContextHolder.getContext().setAuthentication(shareSessionService.authenticationFor(principal));
        return principal.guestId();
    }

    public String requireExperimentActor(String workspaceId) {
        return requireWorkspaceActor(workspaceId);
    }

    private boolean isRealUser(Authentication auth) {
        return auth != null && auth.getName() != null && !auth.getName().isBlank()
                && !"anonymousUser".equalsIgnoreCase(auth.getName())
                && !auth.getName().startsWith(WorkspaceShareSessionService.AUTH_NAME_PREFIX)
                && !auth.getName().startsWith("guest_");
    }

    private String shareSessionToken() {
        HttpServletRequest request = currentRequest();
        if (request == null) {
            throw new InsufficientAuthenticationException("Share session token is required");
        }
        String authorization = request.getHeader("Authorization");
        if (authorization != null) {
            if (authorization.startsWith("ShareSession ")) {
                return authorization.substring("ShareSession ".length()).trim();
            }
            if (authorization.startsWith("Bearer guest_sess_")) {
                return authorization.substring("Bearer ".length()).trim();
            }
        }
        String queryToken = request.getParameter("sessionToken");
        if (queryToken != null && !queryToken.isBlank()) {
            return queryToken.trim();
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName() != null && auth.getName().startsWith(WorkspaceShareSessionService.AUTH_NAME_PREFIX)) {
            return auth.getName().substring(WorkspaceShareSessionService.AUTH_NAME_PREFIX.length());
        }
        throw new InsufficientAuthenticationException("Share session token is required");
    }

    private HttpServletRequest currentRequest() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs) {
            return attrs.getRequest();
        }
        return null;
    }
}
