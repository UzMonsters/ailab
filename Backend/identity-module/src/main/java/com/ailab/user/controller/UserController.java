package com.ailab.user.controller;

import com.ailab.user.api.UserDtos;
import com.ailab.user.service.UserAccountService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

    @PutMapping("/me")
    public UserDtos.SuccessResponse update(@AuthenticationPrincipal String userId, @Valid @RequestBody UserDtos.UpdateProfileRequest request) {
        service.updateProfile(userId, request.username(), request.avatarUrl());
        return new UserDtos.SuccessResponse(true);
    }

    @GetMapping("/me/preferences")
    public UserDtos.PreferencesResponse preferences(@AuthenticationPrincipal String userId) {
        return service.getPreferences(userId);
    }

    @PutMapping("/me/preferences")
    public UserDtos.SuccessResponse updatePreferences(@AuthenticationPrincipal String userId,
                                                      @Valid @RequestBody UserDtos.UpdatePreferencesRequest request) {
        service.updatePreferences(userId, request);
        return new UserDtos.SuccessResponse(true);
    }

    @GetMapping("/me/statistics")
    public UserDtos.StatisticsResponse statistics(@AuthenticationPrincipal String userId) {
        return service.getStatistics(userId);
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
    public UserDtos.SuccessResponse removeAvatar(@AuthenticationPrincipal String userId) {
        service.removeAvatar(userId);
        return new UserDtos.SuccessResponse(true);
    }

    public record AvatarRequest(@NotBlank String avatarUrl) {
    }
}
