package com.ailab.user.api;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import com.ailab.user.domain.Role;
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

    public record SuccessResponse(boolean success) {
    }
}
