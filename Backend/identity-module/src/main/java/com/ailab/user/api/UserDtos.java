package com.ailab.user.api;

import com.ailab.user.domain.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public final class UserDtos {
    private UserDtos() {
    }

    public record UserMeResponse(String id, String username, String email, String avatarUrl, int level, long xp,
                                 String language, String theme, Map<String, Object> applicationSettings,
                                 Map<String, Long> statistics, List<String> achievements) {
        public UserMeResponse(String id, String username, String email, String avatarUrl, int level, long xp) {
            this(id, username, email, avatarUrl, level, xp, "en", "light", Map.of(), Map.of(), List.of());
        }
    }

    public record PublicUserResponse(String id, String username, String avatarUrl, int level, long xp) {
    }

    public record UpdateProfileRequest(@Size(min = 3, max = 50) String username, @Size(max = 500) String avatarUrl) {
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
