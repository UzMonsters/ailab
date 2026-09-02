package com.ailab.admin.permissions;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AdminMeControllerTest {

    private final AdminMeController controller = new AdminMeController();

    @Test
    void testGetPermissions() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "admin-1", "pass", List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        Map<String, Object> result = controller.getPermissions(auth);

        assertThat(result.get("userId")).isEqualTo("admin-1");
        assertThat(result.get("roles")).isEqualTo(List.of("ADMIN"));
        @SuppressWarnings("unchecked")
        List<String> perms = (List<String>) result.get("permissions");
        assertThat(perms).contains("users:read", "settings:write", "catalog:publish", "audit:read");
    }
}
