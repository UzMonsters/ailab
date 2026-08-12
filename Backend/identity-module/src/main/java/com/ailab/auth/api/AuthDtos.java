package com.ailab.auth.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class AuthDtos {
    private AuthDtos() {
    }

    public record RegisterRequest(@NotBlank @Size(min = 3, max = 50) String username, @NotBlank @Email String email,
                                  @NotBlank @Size(min = 8, max = 100) String password) {
    }

    public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) {
    }

    public record LogoutRequest(@NotBlank String refreshToken) {
    }

    public record RegisterResponse(String id, String username, String email) {
    }

    public record TokenResponse(String accessToken, String refreshToken, long expiresIn, String tokenType) {
        public TokenResponse(String accessToken, long expiresIn, String tokenType) {
            this(accessToken, null, expiresIn, tokenType);
        }
    }

    public record AuthenticationResult(TokenResponse response, String refreshToken) {
    }

    public record SuccessResponse(boolean success) {
    }
}