package com.ailab.common.api;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.mock.web.MockHttpServletRequest;
import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final HttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/auth/login");

    @Test
    void mapsBadCredentialsToUnauthorized() {
        var response = handler.unauthorized(new BadCredentialsException("Invalid credentials"), request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().message()).isEqualTo("Invalid credentials");
    }

    @Test
    void keepsGenericIllegalArgumentAsBadRequest() {
        var response = handler.badRequest(new IllegalArgumentException("Invalid or expired refresh token"), request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
