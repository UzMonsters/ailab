package com.ailab.user.controller;

import com.ailab.user.api.UserDtos;
import com.ailab.user.service.UserAccountService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    private final UserAccountService service;

    public UserController(UserAccountService service) {
        this.service = service;
    }

    @GetMapping("/me")
    public UserDtos.UserMeResponse me(@AuthenticationPrincipal String userId) {
        return service.getMe(userId);
    }

    @PatchMapping("/me")
    public UserDtos.UserMeResponse patch(@AuthenticationPrincipal String userId,
                                         @Valid @RequestBody UserDtos.PatchProfileRequest request,
                                         @RequestHeader(value = "If-Match", required = false) String ifMatch) {
        return service.patchProfile(userId, request, ifMatch);
    }

    @PutMapping("/me")
    public UserDtos.SuccessResponse update(@AuthenticationPrincipal String userId,
                                           @Valid @RequestBody UserDtos.UpdateProfileRequest request) {
        service.updateProfile(userId, request.username(), request.avatarUrl());
        return new UserDtos.SuccessResponse(true);
    }

    @GetMapping("/me/preferences")
    public UserDtos.PreferencesResponse preferences(@AuthenticationPrincipal String userId) {
        return service.getPreferences(userId);
    }

    @PutMapping("/me/preferences")
    public UserDtos.PreferencesResponse updatePreferences(@AuthenticationPrincipal String userId,
                                                          @Valid @RequestBody UserDtos.UpdatePreferencesRequest request) {
        service.updatePreferences(userId, request);
        return service.getPreferences(userId);
    }

    @GetMapping("/me/statistics")
    public UserDtos.StatisticsResponse statistics(@AuthenticationPrincipal String userId) {
        return service.getStatistics(userId);
    }

    @GetMapping("/me/learning-progress")
    public UserDtos.LearningProgressResponse learningProgress(@AuthenticationPrincipal String userId,
                                                             @RequestParam(value = "track", defaultValue = "chemistry") String track) {
        return service.getLearningProgress(userId, track);
    }

    @PostMapping("/me/avatar/upload-urls")
    public UserDtos.AvatarUploadTicketResponse createAvatarUploadTicket(@AuthenticationPrincipal String userId,
                                                                        @Valid @RequestBody UserDtos.AvatarUploadTicketRequest request) {
        return service.createAvatarUploadTicket(userId, request);
    }

    @PostMapping("/me/avatar/complete")
    public UserDtos.AvatarCompleteResponse completeAvatarUpload(@AuthenticationPrincipal String userId,
                                                                @Valid @RequestBody UserDtos.AvatarCompleteRequest request) {
        return service.completeAvatarUpload(userId, request);
    }

    @PostMapping("/me/re-auth")
    public UserDtos.ReAuthResponse reAuth(@AuthenticationPrincipal String userId,
                                          @Valid @RequestBody UserDtos.ReAuthRequest request) {
        return service.reAuthenticate(userId, request);
    }

    @PostMapping("/me/password/change")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@AuthenticationPrincipal String userId,
                               @Valid @RequestBody UserDtos.ChangePasswordRequest request) {
        service.changePassword(userId, request);
    }

    @PostMapping("/me/email-change")
    public ResponseEntity<UserDtos.EmailChangeResponse> requestEmailChange(@AuthenticationPrincipal String userId,
                                                                          @Valid @RequestBody UserDtos.EmailChangeRequest request) {
        UserDtos.EmailChangeResponse response = service.requestEmailChange(userId, request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping("/me/sessions")
    public UserDtos.SessionListResponse sessions(@AuthenticationPrincipal String userId,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        return service.getUserSessions(userId, page, size);
    }

    @DeleteMapping("/me/sessions/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void revokeSession(@AuthenticationPrincipal String userId, @PathVariable String id) {
        service.revokeUserSession(userId, id);
    }

    @PostMapping("/me/deletion-requests")
    public ResponseEntity<UserDtos.AccountDeletionResponse> createDeletionRequest(@AuthenticationPrincipal String userId,
                                                                                 @Valid @RequestBody UserDtos.AccountDeletionRequest request) {
        UserDtos.AccountDeletionResponse response = service.scheduleAccountDeletion(userId, request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @DeleteMapping("/me/deletion-requests/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelDeletionRequest(@AuthenticationPrincipal String userId, @PathVariable String id) {
        service.cancelAccountDeletion(userId, id);
    }

    @DeleteMapping("/me")
    public UserDtos.SuccessResponse delete(@AuthenticationPrincipal String userId) {
        service.delete(userId);
        return new UserDtos.SuccessResponse(true);
    }

    @GetMapping("/{id}")
    public UserDtos.PublicUserResponse publicProfile(@PathVariable @NotBlank String id) {
        return service.getPublic(id);
    }

    @PutMapping("/avatar")
    public UserDtos.SuccessResponse avatar(@AuthenticationPrincipal String userId, @Valid @RequestBody AvatarRequest request) {
        service.updateAvatar(userId, request.avatarUrl());
        return new UserDtos.SuccessResponse(true);
    }

    @DeleteMapping("/avatar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAvatar(@AuthenticationPrincipal String userId) {
        service.removeAvatar(userId);
    }

    public record AvatarRequest(@NotBlank String avatarUrl) {
    }
}
