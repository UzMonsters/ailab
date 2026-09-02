package com.ailab.user.controller;

import com.ailab.user.api.UserDtos;
import com.ailab.user.service.UserAccountService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
public class AdminUserController {
    private final UserAccountService service;

    public AdminUserController(UserAccountService service) {
        this.service = service;
    }

    @GetMapping
    public UserDtos.AdminUserListResponse listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant createdFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant createdTo,
            @RequestParam(required = false) String sort) {
        return service.getUsersPaged(page, size, q, role, status, createdFrom, createdTo, sort);
    }

    @GetMapping("/{id}")
    public UserDtos.AdminUserDetailResponse get(@PathVariable String id) {
        return service.getAdminUserDetail(id);
    }

    @PatchMapping("/{id}")
    public UserDtos.AdminUserListItem patchUser(
            @PathVariable String id,
            @RequestBody UserDtos.AdminPatchUserRequest request,
            @RequestHeader(value = "If-Match", required = false) String ifMatch) {
        return service.patchAdminUser(id, request, ifMatch);
    }

    @PutMapping("/{id}")
    public UserDtos.SuccessResponse update(
            @PathVariable String id,
            @Valid @RequestBody UserDtos.AdminUpdateUserRequest request) {
        service.updateAdminUser(id, request);
        return new UserDtos.SuccessResponse(true);
    }

    @PostMapping("/{id}/block")
    public UserDtos.AdminBlockUserResponse block(
            @PathVariable String id,
            @RequestBody UserDtos.AdminBlockUserRequest request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {
        return service.blockUser(id, request, idempotencyKey);
    }

    @PostMapping("/{id}/unblock")
    public UserDtos.AdminUnblockUserResponse unblock(
            @PathVariable String id,
            @RequestBody UserDtos.AdminUnblockUserRequest request) {
        return service.unblockUser(id, request);
    }

    @GetMapping("/{id}/activity")
    public UserDtos.UserActivityListResponse activity(
            @PathVariable String id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return service.getUserActivity(id, from, to, type, page, size);
    }

    @GetMapping("/{id}/learning-progress")
    public UserDtos.UserLearningProgressResponse learningProgress(
            @PathVariable String id,
            @RequestParam(required = false) String track) {
        return service.getUserLearningProgress(id, track);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserDtos.AdminDeleteUserResponse> delete(
            @PathVariable String id,
            @RequestBody(required = false) UserDtos.AdminDeleteUserRequest request) {
        UserDtos.AdminDeleteUserResponse res = service.deleteUserAdmin(id, request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(res);
    }
}
