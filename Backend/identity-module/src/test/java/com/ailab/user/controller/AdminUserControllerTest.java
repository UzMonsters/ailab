package com.ailab.user.controller;

import com.ailab.user.api.UserDtos;
import com.ailab.user.domain.Role;
import com.ailab.user.service.UserAccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUserControllerTest {
    @Mock UserAccountService service;
    @InjectMocks AdminUserController controller;

    @Test
    void delegatesAdminUserManagement() {
        UserDtos.AdminUserResponse user = new UserDtos.AdminUserResponse(
                "usr_1", "alice", "alice@example.com", Role.USER, null, 1, 0,
                "en", "light", java.util.Map.of(), java.util.Map.of(), List.of());
        when(service.getAllUsers()).thenReturn(List.of(user));
        when(service.getAdminUser("usr_1")).thenReturn(user);
        UserDtos.AdminUpdateUserRequest update = new UserDtos.AdminUpdateUserRequest("alice-new", "alice@example.com", Role.ADMIN);

        assertThat(controller.all()).containsExactly(user);
        assertThat(controller.get("usr_1")).isEqualTo(user);
        assertThat(controller.update("usr_1", update)).isEqualTo(new UserDtos.SuccessResponse(true));
        assertThat(controller.delete("usr_1")).isEqualTo(new UserDtos.SuccessResponse(true));

        verify(service).updateAdminUser("usr_1", update);
        verify(service).deleteAdminUser("usr_1");
    }
}
