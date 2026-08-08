package com.ailab.auth.security;

import com.ailab.user.domain.User;
import com.ailab.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import java.time.Duration;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {
    private static final String SECRET = "test-secret-that-is-at-least-256-bits-long-123456";
    @Mock FilterChain chain;

    @AfterEach
    void clearSecurityContext() { SecurityContextHolder.clearContext(); }

    @Test
    void authenticatesRequestWithValidBearerToken() throws Exception {
        JwtService jwtService = new JwtService(SECRET, Duration.ofMinutes(15));
        User user = new User("alice", "alice@example.com", "hash");
        String token = jwtService.issue(user);
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService, userRepository);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);

        filter.doFilter(request, new MockHttpServletResponse(), chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo(user.getId());
        assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities())
                .extracting(authority -> authority.getAuthority()).containsExactly("ROLE_USER");
        verify(chain).doFilter(eq(request), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void leavesRequestUnauthenticatedWithInvalidBearerToken() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(new JwtService(SECRET, Duration.ofMinutes(15)), mock(UserRepository.class));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalid-token");

        filter.doFilter(request, new MockHttpServletResponse(), chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(chain).doFilter(eq(request), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void rejectsTokenAfterUserSessionVersionChanges() throws Exception {
        JwtService jwtService = new JwtService(SECRET, Duration.ofMinutes(15));
        User user = new User("alice", "alice@example.com", "hash");
        String token = jwtService.issue(user);
        user.incrementTokenVersion();
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService, userRepository);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);

        filter.doFilter(request, new MockHttpServletResponse(), chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}
