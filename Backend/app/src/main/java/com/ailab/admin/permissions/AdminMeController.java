package com.ailab.admin.permissions;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/me")
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
public class AdminMeController {

    @GetMapping("/permissions")
    public Map<String, Object> getPermissions(Authentication authentication) {
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(a -> a.startsWith("ROLE_") ? a.substring(5) : a)
                .toList();

        List<String> permissions = List.of(
                "dashboard.read",
                "dashboard.export",
                "laboratories.read",
                "laboratories.terminate",
                "laboratory:read",
                "laboratory:control",
                "settings.read",
                "settings.write",
                "settings.restore",
                "settings:read",
                "settings:write",
                "assets.read",
                "assets.write",
                "audit.read",
                "audit.export",
                "audit:read",
                "audit:export",
                "users.read",
                "users.write",
                "users.delete",
                "users:read",
                "users:write",
                "users:delete",
                "learning.read",
                "learning.write",
                "learning.publish",
                "levels:read",
                "levels:write",
                "levels:publish",
                "books.read",
                "books.write",
                "books.publish",
                "book:read",
                "book:write",
                "book:publish",
                "catalog.read",
                "catalog.write",
                "catalog.publish",
                "catalog:read",
                "catalog:write",
                "catalog:publish",
                "reports:export"
        ).stream().distinct().sorted().toList();

        return Map.of(
                "userId", authentication.getName(),
                "roles", roles,
                "permissions", permissions
        );
    }
}
