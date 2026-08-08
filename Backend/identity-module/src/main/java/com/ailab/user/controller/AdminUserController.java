package com.ailab.user.controller;

import com.ailab.user.api.UserDtos;
import com.ailab.user.service.UserAccountService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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
    public List<UserDtos.AdminUserResponse> all() {
        return service.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDtos.AdminUserResponse get(@PathVariable String id) {
        return service.getAdminUser(id);
    }

    @PutMapping("/{id}")
    public UserDtos.SuccessResponse update(@PathVariable String id,
                                           @Valid @RequestBody UserDtos.AdminUpdateUserRequest request) {
        service.updateAdminUser(id, request);
        return new UserDtos.SuccessResponse(true);
    }

    @DeleteMapping("/{id}")
    public UserDtos.SuccessResponse delete(@PathVariable String id) {
        service.deleteAdminUser(id);
        return new UserDtos.SuccessResponse(true);
    }
}
