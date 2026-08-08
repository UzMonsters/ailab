package com.ailab.auth.service;

import com.ailab.auth.api.AuthDtos;
import com.ailab.auth.security.AccessTokenIssuer;
import com.ailab.auth.token.RefreshTokenService;
import com.ailab.auth.token.RefreshTokenOperations;
import com.ailab.user.domain.User;
import com.ailab.user.service.UserAccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
    @Mock UserAccountService users;
    @Mock AuthenticationManager authenticationManager;
    @Mock AccessTokenIssuer jwtService;
    @Mock RefreshTokenOperations refreshTokens;
    @InjectMocks AuthServiceImpl service;

    @Test
    void registersUserAndReturnsPublicResponse() {
        User user = new User("alice", "alice@example.com", "hash");
        when(users.register("alice", "alice@example.com", "password")).thenReturn(user);

        AuthDtos.RegisterResponse response = service.register(
                new AuthDtos.RegisterRequest("alice", "alice@example.com", "password"));

        assertThat(response.id()).isEqualTo(user.getId());
        assertThat(response.username()).isEqualTo("alice");
        assertThat(response.email()).isEqualTo("alice@example.com");
    }

    @Test
    void logsInWithValidPasswordAndIssuesAccessAndRefreshTokens() {
        User user = new User("alice", "alice@example.com", "encoded-password");
        when(users.findByEmail("alice@example.com")).thenReturn(user);
        when(jwtService.issue(user)).thenReturn("access-token");
        when(refreshTokens.issue(user.getId())).thenReturn(new RefreshTokenService.IssuedToken(user.getId(), "refresh-token"));
        when(jwtService.expiresInSeconds()).thenReturn(900L);

        AuthDtos.AuthenticationResult result = service.login(new AuthDtos.LoginRequest("alice@example.com", "password"));

        assertThat(result.response()).isEqualTo(new AuthDtos.TokenResponse("access-token", "refresh-token", 900L, "Bearer"));
        assertThat(result.refreshToken()).isEqualTo("refresh-token");
        verify(authenticationManager).authenticate(any());
    }

    @Test
    void rejectsInvalidPassword() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThatThrownBy(() -> service.login(new AuthDtos.LoginRequest("alice@example.com", "wrong")))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid credentials");
        verifyNoInteractions(users, jwtService, refreshTokens);
    }

    @Test
    void refreshesByConsumingOldTokenAndIssuingNewTokens() {
        User user = new User("alice", "alice@example.com", "hash");
        when(refreshTokens.rotate("old-refresh")).thenReturn(new RefreshTokenService.IssuedToken(user.getId(), "new-refresh"));
        when(users.findById(user.getId())).thenReturn(user);
        when(jwtService.issue(user)).thenReturn("new-access");
        when(jwtService.expiresInSeconds()).thenReturn(900L);

        AuthDtos.AuthenticationResult result = service.refresh("old-refresh");

        assertThat(result.response().accessToken()).isEqualTo("new-access");
        assertThat(result.refreshToken()).isEqualTo("new-refresh");
        verify(refreshTokens).rotate("old-refresh");
    }

    @Test
    void logsOutByRevokingRefreshToken() {
        when(refreshTokens.revoke("refresh-token")).thenReturn(java.util.Optional.of("usr_1"));
        service.logout("refresh-token");
        verify(refreshTokens).revoke("refresh-token");
        verify(users).invalidateSessions("usr_1");
    }
}
