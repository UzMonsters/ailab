package com.ailab.user.service;

import com.ailab.user.api.UserDtos;
import com.ailab.user.domain.User;

import java.time.Instant;
import java.util.List;

public interface UserAccountService {
    User register(String username, String email, String rawPassword);

    User findByEmail(String email);

    User findById(String id);

    UserDtos.UserMeResponse getMe(String id);

    UserDtos.UserMeResponse patchProfile(String id, UserDtos.PatchProfileRequest request, String ifMatch);

    UserDtos.PreferencesResponse getPreferences(String id);

    void updatePreferences(String id, UserDtos.UpdatePreferencesRequest request);

    UserDtos.StatisticsResponse getStatistics(String id);

    UserDtos.PublicUserResponse getPublic(String id);

    void updateProfile(String id, String username, String avatarUrl);

    void delete(String id);

    void updateAvatar(String id, String avatarUrl);

    void removeAvatar(String id);

    void invalidateSessions(String id);

    UserDtos.LearningProgressResponse getLearningProgress(String id, String track);

    UserDtos.AvatarUploadTicketResponse createAvatarUploadTicket(String id, UserDtos.AvatarUploadTicketRequest request);

    UserDtos.AvatarCompleteResponse completeAvatarUpload(String id, UserDtos.AvatarCompleteRequest request);

    UserDtos.ReAuthResponse reAuthenticate(String id, UserDtos.ReAuthRequest request);

    void changePassword(String id, UserDtos.ChangePasswordRequest request);

    UserDtos.EmailChangeResponse requestEmailChange(String id, UserDtos.EmailChangeRequest request);

    UserDtos.SessionListResponse getUserSessions(String id, int page, int size);

    void revokeUserSession(String id, String sessionId);

    UserDtos.AccountDeletionResponse scheduleAccountDeletion(String id, UserDtos.AccountDeletionRequest request);

    void cancelAccountDeletion(String id, String deletionId);

    List<UserDtos.AdminUserResponse> getAllUsers();

    UserDtos.AdminUserResponse getAdminUser(String id);

    void updateAdminUser(String id, UserDtos.AdminUpdateUserRequest request);

    void deleteAdminUser(String id);

    UserDtos.AdminUserListResponse getUsersPaged(int page, int size, String q, String role, String status,
                                                Instant createdFrom, Instant createdTo, String sort);

    UserDtos.AdminUserDetailResponse getAdminUserDetail(String id);

    UserDtos.AdminUserListItem patchAdminUser(String id, UserDtos.AdminPatchUserRequest request, String ifMatch);

    UserDtos.AdminBlockUserResponse blockUser(String id, UserDtos.AdminBlockUserRequest request, String idempotencyKey);

    UserDtos.AdminUnblockUserResponse unblockUser(String id, UserDtos.AdminUnblockUserRequest request);

    UserDtos.UserActivityListResponse getUserActivity(String id, Instant from, Instant to, String type, int page, int size);

    UserDtos.UserLearningProgressResponse getUserLearningProgress(String id, String track);

    UserDtos.AdminDeleteUserResponse deleteUserAdmin(String id, UserDtos.AdminDeleteUserRequest request);
}
