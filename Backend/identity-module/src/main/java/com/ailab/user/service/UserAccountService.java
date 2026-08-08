package com.ailab.user.service;

import com.ailab.user.api.UserDtos;
import com.ailab.user.domain.User;
import java.util.List;

public interface UserAccountService {
    User register(String username, String email, String rawPassword);

    User findByEmail(String email);

    User findById(String id);

    UserDtos.UserMeResponse getMe(String id);

    UserDtos.PreferencesResponse getPreferences(String id);

    void updatePreferences(String id, UserDtos.UpdatePreferencesRequest request);

    UserDtos.StatisticsResponse getStatistics(String id);

    UserDtos.PublicUserResponse getPublic(String id);

    void updateProfile(String id, String username, String avatarUrl);

    void delete(String id);

    void updateAvatar(String id, String avatarUrl);

    void removeAvatar(String id);

    void invalidateSessions(String id);

    List<UserDtos.AdminUserResponse> getAllUsers();

    UserDtos.AdminUserResponse getAdminUser(String id);

    void updateAdminUser(String id, UserDtos.AdminUpdateUserRequest request);

    void deleteAdminUser(String id);
}
