package com.ailab.learning.controller;

import com.ailab.learning.dto.LearningDtos.UserLearningProgressDto;
import com.ailab.learning.service.LearningProgressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users/me/learning-progress")
@Tag(name = "User Learning Progress", description = "User Learning Progress Synchronization")
@SecurityRequirement(name = "bearerAuth")
public class UserLearningProgressController {

    private final LearningProgressService progressService;

    public UserLearningProgressController(LearningProgressService progressService) {
        this.progressService = progressService;
    }

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || auth.getName().isBlank() || "anonymousUser".equalsIgnoreCase(auth.getName())) {
            throw new org.springframework.security.authentication.InsufficientAuthenticationException("User must be authenticated");
        }
        return auth.getName();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get User Learning Progress")
    public UserLearningProgressDto getUserProgress(
            @RequestParam(required = false, defaultValue = "chemistry") String track
    ) {
        return progressService.getUserProgress(getCurrentUserId(), track);
    }
}
