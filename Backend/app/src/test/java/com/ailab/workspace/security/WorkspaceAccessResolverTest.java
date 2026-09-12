package com.ailab.workspace.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WorkspaceAccessResolverTest {

    static {
        System.setProperty("net.bytebuddy.experimental", "true");
    }

    private TestWorkspaceShareSessionService shareSessionService;
    private MockHttpServletRequest request;
    private WorkspaceAccessResolver resolver;

    @BeforeEach
    void setUp() {
        shareSessionService = new TestWorkspaceShareSessionService();
        resolver = new WorkspaceAccessResolver(shareSessionService);
        request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
        SecurityContextHolder.clearContext();
    }

    @Test
    void testQueryParameterSessionTokenIsIgnoredAndRejected() {
        // Query param sessionToken must NOT be accepted (BE-004)
        request.setParameter("sessionToken", "leaked-query-param-token");

        assertThatThrownBy(() -> resolver.requireWorkspaceActor("ws-123"))
                .isInstanceOf(InsufficientAuthenticationException.class)
                .hasMessageContaining("Share session token is required");
    }

    @Test
    void testAuthorizationShareSessionHeaderAccepted() {
        request.addHeader("Authorization", "ShareSession secret-token-456");

        String actor = resolver.requireWorkspaceActor("ws-123");
        assertThat(actor).isEqualTo("guest-user-1");
    }

    @Test
    void testXShareSessionHeaderAccepted() {
        request.addHeader("X-Share-Session", "header-token-789");

        String actor = resolver.requireWorkspaceActor("ws-123");
        assertThat(actor).isEqualTo("guest-user-2");
    }

    private static class TestWorkspaceShareSessionService extends WorkspaceShareSessionService {
        public TestWorkspaceShareSessionService() {
            super(null, null, "12345678901234567890123456789012", 3600);
        }

        @Override
        public ShareSessionPrincipal validate(String token, String workspaceId) {
            if ("secret-token-456".equals(token) && "ws-123".equals(workspaceId)) {
                return new ShareSessionPrincipal("guest-user-1", "link-1", "ws-123", "EDITOR", List.of("READ", "WRITE"), Instant.now().plusSeconds(3600));
            }
            if ("header-token-789".equals(token) && "ws-123".equals(workspaceId)) {
                return new ShareSessionPrincipal("guest-user-2", "link-2", "ws-123", "VIEWER", List.of("READ"), Instant.now().plusSeconds(3600));
            }
            throw new InsufficientAuthenticationException("Invalid share session token");
        }
    }
}
