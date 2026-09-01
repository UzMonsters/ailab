package com.ailab.workspace.dto;

import java.time.Instant;

public record WorkspaceMemberDto(
        String userId,
        String displayName,
        String emailMasked,
        String avatarUrl,
        String role, // OWNER, EDITOR, VIEWER
        String status, // ACTIVE, INVITED
        Instant joinedAt,
        Instant lastSeenAt
) {
    public static String maskEmail(String email) {
        if (email == null || email.isBlank()) return "";
        int at = email.indexOf('@');
        if (at <= 1) return email;
        String name = email.substring(0, at);
        String domain = email.substring(at);
        return name.charAt(0) + "***" + domain;
    }
}
