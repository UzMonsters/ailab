package com.ailab.user.service;

import com.ailab.auth.token.RefreshTokenService;
import com.ailab.user.api.UserDtos;
import com.ailab.user.domain.*;
import com.ailab.user.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Transactional
public class UserAccountServiceImpl implements UserAccountService {
    private static final List<String> ALLOWED_MIME_TYPES = List.of("image/jpeg", "image/png", "image/webp");
    private static final long MAX_AVATAR_BYTES = 2097152L;

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher events;
    private final com.ailab.auth.token.RefreshTokenOperations refreshTokenService;
    private final ReAuthTokenOperations reAuthTokenService;

    public UserAccountServiceImpl(UserRepository repository,
                                  PasswordEncoder passwordEncoder,
                                  ApplicationEventPublisher events,
                                  com.ailab.auth.token.RefreshTokenOperations refreshTokenService,
                                  ReAuthTokenOperations reAuthTokenService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.events = events;
        this.refreshTokenService = refreshTokenService;
        this.reAuthTokenService = reAuthTokenService;
    }

    @Override
    public User register(String username, String email, String rawPassword) {
        if (repository.existsByEmailIgnoreCase(email))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already taken");
        if (repository.existsByUsernameIgnoreCase(username))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already taken");
        return repository.save(new User(username, email, passwordEncoder.encode(rawPassword)));
    }

    @Override
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return repository.findByEmailIgnoreCase(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(String id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDtos.UserMeResponse getMe(String id) {
        User u = findById(id);
        return toMeResponse(u);
    }

    @Override
    public UserDtos.UserMeResponse patchProfile(String id, UserDtos.PatchProfileRequest request, String ifMatch) {
        User user = findById(id);
        validateIfMatch(user.getVersion(), ifMatch);

        if (request.username() != null && !request.username().isBlank() && !request.username().equalsIgnoreCase(user.getUsername())) {
            if (repository.existsByUsernameIgnoreCaseAndIdNot(request.username(), id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already taken");
            }
        }

        user.patchProfile(request.username(), request.displayName(), request.bio());
        return toMeResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDtos.PreferencesResponse getPreferences(String id) {
        User u = findById(id);
        return new UserDtos.PreferencesResponse(u.getLanguage(), u.getTheme(), u.getApplicationSettings());
    }

    @Override
    public void updatePreferences(String id, UserDtos.UpdatePreferencesRequest request) {
        findById(id).updatePreferences(request.language(), request.theme(), request.applicationSettings());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDtos.StatisticsResponse getStatistics(String id) {
        User u = findById(id);
        return new UserDtos.StatisticsResponse(u.getStatistics(), u.getAchievements().stream().sorted().toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDtos.PublicUserResponse getPublic(String id) {
        User u = findById(id);
        return new UserDtos.PublicUserResponse(u.getId(), u.getUsername(), u.getAvatarUrl(), u.getLevel(), u.getXp());
    }

    @Override
    public void updateProfile(String id, String username, String avatarUrl) {
        User user = findById(id);
        if (username != null && !username.equalsIgnoreCase(user.getUsername())
                && repository.existsByUsernameIgnoreCaseAndIdNot(username, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already taken");
        }
        user.updateProfile(username, avatarUrl);
    }

    @Override
    public void delete(String id) {
        User user = findById(id);
        user.incrementTokenVersion();
        repository.delete(user);
        events.publishEvent(new UserDeletedEvent(id));
    }

    @Override
    public void updateAvatar(String id, String avatarUrl) {
        findById(id).updateProfile(null, avatarUrl);
    }

    @Override
    public void removeAvatar(String id) {
        findById(id).removeAvatar();
    }

    @Override
    public void invalidateSessions(String id) {
        findById(id).incrementTokenVersion();
    }

    @Override
    @Transactional(readOnly = true)
    public UserDtos.LearningProgressResponse getLearningProgress(String id, String track) {
        User user = findById(id);
        String selectedTrack = (track != null && !track.isBlank()) ? track.trim() : "chemistry";
        List<Map<String, Object>> tracks = List.of(
                Map.of(
                        "id", selectedTrack,
                        "title", "Chemistry Fundamentals",
                        "completedLevels", Math.min(user.getLevel(), 5),
                        "totalLevels", 10,
                        "progressPercentage", Math.min(100, user.getLevel() * 10)
                )
        );
        Instant startedAt = user.getUpdatedAt() != null ? user.getUpdatedAt() : (user.getCreatedAt() != null ? user.getCreatedAt() : Instant.now());
        Map<String, Object> activeAttempt = Map.of(
                "track", selectedTrack,
                "level", user.getLevel(),
                "startedAt", startedAt
        );
        List<String> badges = user.getAchievements().stream().sorted().toList();
        Instant lastActivityAt = user.getLastSeenAt() != null ? user.getLastSeenAt() : startedAt;
        return new UserDtos.LearningProgressResponse(
                Math.min(user.getLevel(), 5),
                activeAttempt,
                badges,
                tracks,
                user.getLevel() * 2,
                lastActivityAt
        );
    }

    @Override
    public UserDtos.AvatarUploadTicketResponse createAvatarUploadTicket(String id, UserDtos.AvatarUploadTicketRequest request) {
        findById(id);

        if (request.mimeType() == null || !ALLOWED_MIME_TYPES.contains(request.mimeType().toLowerCase().trim())) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "UNSUPPORTED_MEDIA_TYPE: Allowed formats are image/jpeg, image/png, image/webp");
        }

        if (request.size() != null && request.size() > MAX_AVATAR_BYTES) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "ASSET_TOO_LARGE: Avatar image size cannot exceed 2MB");
        }

        String assetId = "asset_avatar_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        String uploadUrl = "https://storage.ailab.local/upload/avatar/" + id + "/" + assetId;
        Instant expiresAt = Instant.now().plus(Duration.ofMinutes(15));

        return new UserDtos.AvatarUploadTicketResponse(assetId, uploadUrl, expiresAt, MAX_AVATAR_BYTES, ALLOWED_MIME_TYPES);
    }

    @Override
    public UserDtos.AvatarCompleteResponse completeAvatarUpload(String id, UserDtos.AvatarCompleteRequest request) {
        User user = findById(id);
        if (request.assetId() == null || request.assetId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "VALIDATION_ERROR: Asset ID is required");
        }
        String avatarUrl = "https://cdn.ailab.local/avatar/" + id + "-" + request.assetId().trim() + ".webp";
        user.setAvatarUrl(avatarUrl);
        return new UserDtos.AvatarCompleteResponse(avatarUrl, request.assetId().trim(), user.getUpdatedAt());
    }

    @Override
    public UserDtos.ReAuthResponse reAuthenticate(String id, UserDtos.ReAuthRequest request) {
        User user = findById(id);
        reAuthTokenService.checkRateLimit(id);

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            reAuthTokenService.recordFailedAttempt(id);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        ReAuthTokenService.IssuedReAuthToken token = reAuthTokenService.issueToken(id);
        return new UserDtos.ReAuthResponse(token.token(), token.expiresAt());
    }

    @Override
    public void changePassword(String id, UserDtos.ChangePasswordRequest request) {
        User user = findById(id);
        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid current password");
        }
        user.updatePassword(passwordEncoder.encode(request.newPassword()));
        refreshTokenService.revokeAll(id);
    }

    @Override
    public UserDtos.EmailChangeResponse requestEmailChange(String id, UserDtos.EmailChangeRequest request) {
        User user = findById(id);

        boolean tokenValid = reAuthTokenService.validateAndConsumeToken(id, request.reauthToken());
        if (!tokenValid) {
            if (request.currentPassword() == null || !passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Valid re-authentication token or password is required");
            }
        }

        if (repository.existsByEmailIgnoreCaseAndIdNot(request.newEmail(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already taken");
        }

        user.updateEmail(request.newEmail());
        return new UserDtos.EmailChangeResponse(false, Instant.now().plus(Duration.ofHours(24)), request.newEmail());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDtos.SessionListResponse getUserSessions(String id, int page, int size) {
        findById(id);
        return refreshTokenService.getActiveSessions(id, page, size);
    }

    @Override
    public void revokeUserSession(String id, String sessionId) {
        findById(id);
        refreshTokenService.revokeSession(id, sessionId);
    }

    @Override
    public UserDtos.AccountDeletionResponse scheduleAccountDeletion(String id, UserDtos.AccountDeletionRequest request) {
        User user = findById(id);

        if (request.confirmation() == null || !"DELETE".equals(request.confirmation().trim())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "VALIDATION_ERROR: Confirmation must be exactly 'DELETE'");
        }

        if (!reAuthTokenService.validateAndConsumeToken(id, request.reauthToken())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Valid re-authentication token is required");
        }

        String deletionId = "del_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        Instant scheduledFor = Instant.now().plus(30, ChronoUnit.DAYS);
        user.scheduleDeletion(deletionId, scheduledFor);

        return new UserDtos.AccountDeletionResponse(deletionId, scheduledFor, "DELETION_SCHEDULED");
    }

    @Override
    public void cancelAccountDeletion(String id, String deletionId) {
        User user = findById(id);
        if (user.getDeletionId() == null || !user.getDeletionId().equals(deletionId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Deletion request not found");
        }
        user.cancelDeletion();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDtos.AdminUserResponse> getAllUsers() {
        return repository.findAll().stream().map(this::toAdminResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserDtos.AdminUserResponse getAdminUser(String id) {
        return toAdminResponse(findById(id));
    }

    @Override
    public void updateAdminUser(String id, UserDtos.AdminUpdateUserRequest request) {
        User user = findById(id);
        if (request.username() != null && !request.username().equalsIgnoreCase(user.getUsername())
                && repository.existsByUsernameIgnoreCaseAndIdNot(request.username(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already taken");
        }
        if (request.email() != null && !request.email().equalsIgnoreCase(user.getEmail())
                && repository.existsByEmailIgnoreCaseAndIdNot(request.email(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already taken");
        }
        user.updateAdminProfile(request.username(), request.email(), request.role());
    }

    @Override
    public void deleteAdminUser(String id) {
        User user = findById(id);
        user.incrementTokenVersion();
        repository.delete(user);
        events.publishEvent(new UserDeletedEvent(id));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDtos.AdminUserListResponse getUsersPaged(int page, int size, String q, String role, String status,
                                                        Instant createdFrom, Instant createdTo, String sort) {
        int safePage = Math.max(0, page);
        int safeSize = Math.max(1, Math.min(size, 100));

        Sort sorting = parseSort(sort);
        Pageable pageable = PageRequest.of(safePage, safeSize, sorting);

        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (q != null && !q.isBlank()) {
                String pattern = "%" + q.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("username")), pattern),
                        cb.like(cb.lower(root.get("email")), pattern)
                ));
            }
            if (role != null && !role.isBlank()) {
                try {
                    Role roleEnum = Role.valueOf(role.trim().toUpperCase());
                    predicates.add(cb.equal(root.get("role"), roleEnum));
                } catch (IllegalArgumentException ignored) {
                }
            }
            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(cb.upper(root.get("status")), status.trim().toUpperCase()));
            }
            if (createdFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), createdFrom));
            }
            if (createdTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), createdTo));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<User> userPage = repository.findAll(spec, pageable);
        List<UserDtos.AdminUserListItem> items = userPage.getContent().stream()
                .map(this::toAdminListItem)
                .toList();

        Map<String, Object> facets = Map.of(
                "roles", Map.of(
                        "STUDENT", repository.countByRole(Role.USER),
                        "TEACHER", 0L,
                        "ADMIN", repository.countByRole(Role.ADMIN)
                ),
                "statuses", Map.of(
                        "ACTIVE", repository.countByStatus("ACTIVE"),
                        "BLOCKED", repository.countByStatus("BLOCKED"),
                        "DEACTIVATED", repository.countByStatus("DEACTIVATED")
                )
        );

        UserDtos.PageMetadata pageMetadata = new UserDtos.PageMetadata(
                userPage.getNumber(),
                userPage.getSize(),
                userPage.getTotalElements(),
                userPage.getTotalPages()
        );

        return new UserDtos.AdminUserListResponse(items, pageMetadata, facets);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDtos.AdminUserDetailResponse getAdminUserDetail(String id) {
        User user = findById(id);
        Map<String, Object> userMap = new LinkedHashMap<>();
        userMap.put("id", user.getId());
        userMap.put("username", user.getUsername());
        userMap.put("displayName", user.getDisplayName() != null ? user.getDisplayName() : user.getUsername());
        userMap.put("bio", user.getBio());
        userMap.put("email", user.getEmail());
        userMap.put("avatarUrl", user.getAvatarUrl());
        userMap.put("level", user.getLevel());
        userMap.put("xp", user.getXp());
        userMap.put("language", user.getLanguage());
        userMap.put("theme", user.getTheme());

        return new UserDtos.AdminUserDetailResponse(
                userMap,
                List.of(user.getRole()),
                user.getStatus(),
                user.getStatistics(),
                user.getCreatedAt(),
                user.getLastSeenAt() != null ? user.getLastSeenAt() : user.getUpdatedAt(),
                user.getVersion()
        );
    }

    @Override
    public UserDtos.AdminUserListItem patchAdminUser(String id, UserDtos.AdminPatchUserRequest request, String ifMatch) {
        User user = findById(id);
        validateIfMatch(user.getVersion(), ifMatch);

        Role targetRole = request.role();
        if (targetRole == null && request.roles() != null && !request.roles().isEmpty()) {
            targetRole = request.roles().get(0);
        }

        boolean roleChanged = targetRole != null && targetRole != user.getRole();
        boolean statusChanged = request.status() != null && !request.status().equalsIgnoreCase(user.getStatus());

        if ((roleChanged || statusChanged) && (request.reason() == null || request.reason().isBlank())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "VALIDATION_ERROR: Reason is required when changing role or status");
        }

        if (request.username() != null && !request.username().equalsIgnoreCase(user.getUsername())) {
            if (repository.existsByUsernameIgnoreCaseAndIdNot(request.username(), id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already taken");
            }
        }

        user.updateAdminUser(request.username(), targetRole, request.status(), request.reason());
        return toAdminListItem(user);
    }

    @Override
    public UserDtos.AdminBlockUserResponse blockUser(String id, UserDtos.AdminBlockUserRequest request, String idempotencyKey) {
        if (request.reason() == null || request.reason().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "VALIDATION_ERROR: Reason is required for blocking a user");
        }
        User user = findById(id);
        user.block(request.reason(), request.until());
        return new UserDtos.AdminBlockUserResponse(user.getId(), "BLOCKED", 1);
    }

    @Override
    public UserDtos.AdminUnblockUserResponse unblockUser(String id, UserDtos.AdminUnblockUserRequest request) {
        if (request.reason() == null || request.reason().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "VALIDATION_ERROR: Reason is required for unblocking a user");
        }
        User user = findById(id);
        user.unblock(request.reason());
        return new UserDtos.AdminUnblockUserResponse(user.getId(), "ACTIVE");
    }

    @Override
    @Transactional(readOnly = true)
    public UserDtos.UserActivityListResponse getUserActivity(String id, Instant from, Instant to, String type, int page, int size) {
        User user = findById(id);
        int safePage = Math.max(0, page);
        int safeSize = Math.max(1, Math.min(size, 100));

        List<UserDtos.UserActivity> all = new ArrayList<>();
        Instant base = user.getCreatedAt();
        if (base != null) {
            all.add(new UserDtos.UserActivity(
                    "act_" + user.getId().substring(4) + "_1",
                    base,
                    "AUTH",
                    "user.registered",
                    "Account registered",
                    "127.0.0.1",
                    "Mozilla/5.0"
            ));
        }
        if (user.getUpdatedAt() != null && !user.getUpdatedAt().equals(base)) {
            all.add(new UserDtos.UserActivity(
                    "act_" + user.getId().substring(4) + "_2",
                    user.getUpdatedAt(),
                    "PROFILE",
                    "user.profile_updated",
                    "Profile or preferences updated",
                    "127.0.0.1",
                    "Mozilla/5.0"
            ));
        }

        List<UserDtos.UserActivity> filtered = all.stream()
                .filter(a -> from == null || !a.occurredAt().isBefore(from))
                .filter(a -> to == null || !a.occurredAt().isAfter(to))
                .filter(a -> type == null || a.type().equalsIgnoreCase(type))
                .toList();

        int fromIndex = Math.min(safePage * safeSize, filtered.size());
        int toIndex = Math.min(fromIndex + safeSize, filtered.size());
        List<UserDtos.UserActivity> pagedItems = filtered.subList(fromIndex, toIndex);
        int totalPages = (int) Math.ceil((double) filtered.size() / safeSize);

        UserDtos.PageMetadata pageMetadata = new UserDtos.PageMetadata(safePage, safeSize, filtered.size(), totalPages);
        return new UserDtos.UserActivityListResponse(pagedItems, pageMetadata);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDtos.UserLearningProgressResponse getUserLearningProgress(String id, String track) {
        User user = findById(id);
        List<Map<String, Object>> tracks = List.of(
                Map.of(
                        "id", "chemistry-basics",
                        "title", "Chemistry Fundamentals",
                        "completedLevels", Math.min(user.getLevel(), 5),
                        "totalLevels", 10,
                        "progressPercentage", Math.min(100, user.getLevel() * 10)
                )
        );
        return new UserDtos.UserLearningProgressResponse(tracks, user.getLevel() * 2, user.getLevel(), user.getUpdatedAt());
    }

    @Override
    public UserDtos.AdminDeleteUserResponse deleteUserAdmin(String id, UserDtos.AdminDeleteUserRequest request) {
        User user = findById(id);
        String mode = request != null && request.mode() != null ? request.mode() : "DEACTIVATE";
        if ("SCHEDULE_DELETE".equalsIgnoreCase(mode)) {
            Instant scheduled = Instant.now().plus(30, ChronoUnit.DAYS);
            user.deactivate(request != null ? request.reason() : "Deletion scheduled");
            return new UserDtos.AdminDeleteUserResponse("DELETION_SCHEDULED", scheduled);
        } else {
            user.deactivate(request != null ? request.reason() : "Deactivated by admin");
            return new UserDtos.AdminDeleteUserResponse("DEACTIVATED", null);
        }
    }

    private void validateIfMatch(Long currentVersion, String ifMatch) {
        if (ifMatch == null || ifMatch.isBlank()) return;
        String clean = ifMatch.replace("\"", "").replace("W/", "").trim();
        try {
            long parsed = Long.parseLong(clean);
            if (!Objects.equals(currentVersion, parsed)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "VERSION_CONFLICT: Expected version " + parsed + " but current is " + currentVersion);
            }
        } catch (NumberFormatException ignored) {
        }
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        String[] parts = sort.split(",");
        String field = parts[0].trim();
        Sort.Direction direction = parts.length > 1 && "asc".equalsIgnoreCase(parts[1].trim()) ? Sort.Direction.ASC : Sort.Direction.DESC;
        if ("lastActive".equalsIgnoreCase(field) || "lastActiveAt".equalsIgnoreCase(field)) {
            field = "updatedAt";
        } else if ("displayName".equalsIgnoreCase(field)) {
            field = "username";
        }
        return Sort.by(direction, field);
    }

    private UserDtos.AdminUserListItem toAdminListItem(User u) {
        return new UserDtos.AdminUserListItem(
                u.getId(),
                u.getDisplayName() != null ? u.getDisplayName() : u.getUsername(),
                u.getEmail(),
                u.getRole(),
                u.getStatus(),
                u.getLevel(),
                u.getXp(),
                u.getLastSeenAt() != null ? u.getLastSeenAt() : u.getUpdatedAt(),
                u.getCreatedAt(),
                u.getVersion()
        );
    }

    private UserDtos.UserMeResponse toMeResponse(User u) {
        return new UserDtos.UserMeResponse(
                u.getId(),
                u.getUsername(),
                u.getDisplayName() != null ? u.getDisplayName() : u.getUsername(),
                u.getBio(),
                u.getEmail(),
                u.getAvatarUrl(),
                u.getLevel(),
                u.getXp(),
                u.getLanguage(),
                u.getTheme(),
                u.getApplicationSettings(),
                u.getStatistics(),
                u.getAchievements().stream().sorted().toList(),
                u.getCreatedAt(),
                u.getUpdatedAt(),
                u.getVersion()
        );
    }

    private UserDtos.AdminUserResponse toAdminResponse(User u) {
        return new UserDtos.AdminUserResponse(
                u.getId(),
                u.getUsername(),
                u.getEmail(),
                u.getRole(),
                u.getAvatarUrl(),
                u.getLevel(),
                u.getXp(),
                u.getLanguage(),
                u.getTheme(),
                u.getApplicationSettings(),
                u.getStatistics(),
                u.getAchievements().stream().sorted().toList()
        );
    }
}
