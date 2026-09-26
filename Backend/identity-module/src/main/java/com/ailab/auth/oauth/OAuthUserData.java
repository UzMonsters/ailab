package com.ailab.auth.oauth;

public record OAuthUserData(
        String provider,
        String subject,
        String email,
        boolean emailVerified,
        String name,
        String picture
) {
}