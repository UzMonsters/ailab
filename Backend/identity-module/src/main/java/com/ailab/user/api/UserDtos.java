package com.ailab.user.api;

import com.ailab.user.domain.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public final class UserDtos {
    private UserDtos() {
    }

    public record UserMeResponse(
            String id,
            String username,
            String displayName,
            String bio,
            String email,
            String avatarUrl,
            int level,
            long xp,
            String language,
            String theme,
            Map<String, Object> applicationSettings,
            Map<String, Long> statistics,
            List<String> achievements,
            Instant createdAt,
            Instant updatedAt,
            Long version
    ) {
        public UserMeResponse(String id, String username, String email, String avatarUrl, int level, long xp) {
            this(id, username, username, null, email, avatarUrl, level, xp, "en", "light", Map.of(), Map.of(), List.of(), null, null, 0L);
        }

        public UserMeResponse(String id, String username, String email, String avatarUrl, int level, long xp,
                              String language, String theme, Map<String, Object> applicationSettings,
                              Map<String, Long> statistics, List<String> achievements) {
            this(id, username, username, null, email, avatarUrl, level, xp, language, theme, applicationSettings, statistics, achievements, null, null, 0L);
        }
    }

    public record PublicUserResponse(String id, String username, String avatarUrl, int level, long xp) {
    }

    public record UpdateProfileRequest(@Size(min = 3, max = 50) String username, @Size(max = 500) String avatarUrl) {
    }

    public record PatchProfileRequest(
            @Size(min = 3, max = 50) String username,
            @Size(max = 100) String displayName,
            @Size(max = 500) String bio
    ) {
    }

    public record UpdatePreferencesRequest(
            @Pattern(regexp = "^[a-zA-Z]{2,10}$", message = "language must be a valid language code") String language,
            @Pattern(regexp = "^(light|dark|system)$", message = "theme must be light, dark, or system") String theme,
            Map<String, Object> applicationSettings) {
    }

    public record PreferencesResponse(String language, String theme, Map<String, Object> applicationSettings) {
    }

    public record StatisticsResponse(Map<String, Long> statistics, List<String> achievements) {
    }

    public record LearningProgressResponse(
            int completedLevels,
            Object activeAttempt,
            List<String> badges,
            List<Map<String, Object>> tracks,
            int attempts,
            Instant lastActivityAt
    ) {
    }

    public record AvatarUploadTicketRequest(
            @NotBlank String fileName,
            @NotBlank String mimeType,
            Long size,
            String checksum
    ) {
    }

    public record AvatarUploadTicketResponse(
            String assetId,
            String uploadUrl,
            Instant expiresAt,
            long maxBytes,
            List<String> allowedMimeTypes
    ) {
    }

    public record AvatarCompleteRequest(
            @NotBlank String assetId,
            Map<String, Object> crop
    ) {
    }

    public record AvatarCompleteResponse(
            String avatarUrl,
            String assetId,
            Instant updatedAt
    ) {
    }

    public record ReAuthRequest(
            @NotBlank String password
    ) {
    }

    public record ReAuthResponse(
            String reauthToken,
            Instant expiresAt
    ) {
    }

    public record ChangePasswordRequest(
            @NotBlank String currentPassword,
            @NotBlank @Size(min = 8, message = "Password must be at least 8 characters") String newPassword
    ) {
    }

    public record EmailChangeRequest(
            @jakarta.validation.constraints.Email @NotBlank String newEmail,
            String reauthToken,
            String currentPassword
    ) {
    }

    public record EmailChangeResponse(
            boolean verificationRequired,
            Instant expiresAt,
            String pendingEmail
    ) {
    }

    public record SessionItem(
            String id,
            String familyId,
            Instant createdAt,
            Instant expiresAt,
            boolean current,
            String userAgent,
            String ipAddress,
            Instant lastActiveAt
    ) {
    }

    public record SessionListResponse(
            List<SessionItem> items,
            PageMetadata page
    ) {
    }

    public record AccountDeletionRequest(
            String reauthToken,
            @NotBlank String confirmation
    ) {
    }

    public record AccountDeletionResponse(
            String deletionId,
            Instant scheduledFor,
            String status
    ) {
    }

    public record AdminUserResponse(String id, String username, String email, Role role, String avatarUrl,
                                    int level, long xp, String language, String theme,
                                    Map<String, Object> applicationSettings, Map<String, Long> statistics,
                                    List<String> achievements) {
    }

    public record AdminUpdateUserRequest(@Size(min = 3, max = 50) String username,
                                         @jakarta.validation.constraints.Email String email,
                                         Role role) {
    }

    public record AdminUserListItem(String id, String displayName, String email, Role role, String status,
                                    int level, long xp, Instant lastActiveAt, Instant createdAt, Long version) {
    }

    public record PageMetadata(int number, int size, long totalElements, int totalPages) {
    }

    public record AdminUserListResponse(List<AdminUserListItem> items, PageMetadata page, Map<String, Object> facets) {
    }

    public record AdminUserDetailResponse(Map<String, Object> user, List<Role> roles, String status,
                                          Map<String, Long> statistics, Instant createdAt, Instant lastSeenAt, Long version) {
    }

    public record AdminPatchUserRequest(String username, List<Role> roles, Role role, String status, String reason) {
    }

    public record AdminBlockUserRequest(String reason, Instant until) {
    }

    public record AdminBlockUserResponse(String id, String status, int sessionsRevoked) {
    }

    public record AdminUnblockUserRequest(String reason) {
    }

    public record AdminUnblockUserResponse(String id, String status) {
    }

    public record AdminDeleteUserRequest(String reason, String mode) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record AdminDeleteUserResponse(String status, Instant deletionScheduledFor) {
    }

    public record UserActivity(String id, Instant occurredAt, String type, String action, String description, String ip, String userAgent) {
    }

    public record UserActivityListResponse(List<UserActivity> items, PageMetadata page) {
    }

    public record UserLearningProgressResponse(List<Map<String, Object>> tracks, int attempts, int completedLevels, Instant lastActivityAt) {
    }

    public record SuccessResponse(boolean success) {
    }
}
