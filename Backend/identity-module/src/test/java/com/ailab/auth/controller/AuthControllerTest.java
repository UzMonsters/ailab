package com.ailab.auth.controller;

import com.ailab.auth.api.AuthDtos;
import com.ailab.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import java.time.Duration;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
    @Mock AuthService service;
    private AuthController controller;

    @BeforeEach
    void setUp() {
        controller = new AuthController(service);
        ReflectionTestUtils.setField(controller, "refreshCookieName", "refresh_token");
        ReflectionTestUtils.setField(controller, "refreshCookieSecure", true);
        ReflectionTestUtils.setField(controller, "refreshTokenTtl", Duration.ofDays(30));
    }

    @Test
    void delegatesAuthOperationsAndSetsHttpOnlyRefreshCookie() {
        AuthDtos.RegisterRequest registerRequest = new AuthDtos.RegisterRequest("alice", "alice@example.com", "password");
        AuthDtos.RegisterResponse registerResponse = new AuthDtos.RegisterResponse("usr_1", "alice", "alice@example.com");
        AuthDtos.LoginRequest loginRequest = new AuthDtos.LoginRequest("alice@example.com", "password");
        AuthDtos.TokenResponse tokenResponse = new AuthDtos.TokenResponse("access", "refresh", 900, "Bearer");
        AuthDtos.AuthenticationResult result = new AuthDtos.AuthenticationResult(tokenResponse, "refresh");
        AuthDtos.LogoutRequest logoutRequest = new AuthDtos.LogoutRequest("refresh");
        when(service.register(registerRequest)).thenReturn(registerResponse);
        when(service.login(loginRequest)).thenReturn(result);
        when(service.refresh("refresh")).thenReturn(result);

        assertThat(controller.register(registerRequest)).isEqualTo(registerResponse);
        MockHttpServletResponse loginResponse = new MockHttpServletResponse();
        assertThat(controller.login(loginRequest, loginResponse)).isEqualTo(tokenResponse);
        assertThat(loginResponse.getHeader("Set-Cookie")).contains("HttpOnly", "refresh_token=refresh");
        assertThat(controller.refresh(new MockHttpServletRequest(), logoutRequest, new MockHttpServletResponse())).isEqualTo(tokenResponse);
        assertThat(controller.logout(new MockHttpServletRequest(), logoutRequest, new MockHttpServletResponse()))
                .isEqualTo(new AuthDtos.SuccessResponse(true));

        verify(service).register(registerRequest);
        verify(service).login(loginRequest);
        verify(service).refresh("refresh");
        verify(service).logout("refresh");
    }

    @Test
    void readsConfiguredRefreshCookieName() {
        ReflectionTestUtils.setField(controller, "refreshCookieName", "session_refresh");
        AuthDtos.TokenResponse tokenResponse = new AuthDtos.TokenResponse("access", "new-refresh", 900, "Bearer");
        AuthDtos.AuthenticationResult result = new AuthDtos.AuthenticationResult(tokenResponse, "new-refresh");
        when(service.refresh("configured-refresh")).thenReturn(result);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new jakarta.servlet.http.Cookie("session_refresh", "configured-refresh"));

        assertThat(controller.refresh(request, null, new MockHttpServletResponse())).isEqualTo(tokenResponse);
        verify(service).refresh("configured-refresh");
    }
}
