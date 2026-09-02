package com.ailab.user.controller;

import com.ailab.user.api.UserDtos;
import com.ailab.user.domain.Role;
import com.ailab.user.service.UserAccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUserControllerTest {
    @Mock
    UserAccountService service;

    @InjectMocks
    AdminUserController controller;

    @Test
    void delegatesAdminUserManagement() {
        UserDtos.AdminUserListItem item = new UserDtos.AdminUserListItem(
                "usr_1", "alice", "alice@example.com", Role.USER, "ACTIVE", 1, 0,
                Instant.now(), Instant.now(), 0L);
        UserDtos.AdminUserListResponse listResponse = new UserDtos.AdminUserListResponse(
                List.of(item),
                new UserDtos.PageMetadata(0, 10, 1, 1),
                Map.of("roles", Map.of("STUDENT", 1L))
        );
        when(service.getUsersPaged(0, 10, null, null, null, null, null, null)).thenReturn(listResponse);

        UserDtos.AdminUserDetailResponse detail = new UserDtos.AdminUserDetailResponse(
                Map.of("id", "usr_1", "username", "alice"),
                List.of(Role.USER),
                "ACTIVE",
                Map.of(),
                Instant.now(),
                Instant.now(),
                0L
        );
        when(service.getAdminUserDetail("usr_1")).thenReturn(detail);

        UserDtos.AdminPatchUserRequest patchReq = new UserDtos.AdminPatchUserRequest("alice-new", List.of(Role.ADMIN), Role.ADMIN, "ACTIVE", "Role promotion");
        when(service.patchAdminUser("usr_1", patchReq, "0")).thenReturn(item);

        UserDtos.AdminBlockUserRequest blockReq = new UserDtos.AdminBlockUserRequest("Safety violation", null);
        when(service.blockUser("usr_1", blockReq, "idemp_1")).thenReturn(new UserDtos.AdminBlockUserResponse("usr_1", "BLOCKED", 1));

        UserDtos.AdminUnblockUserRequest unblockReq = new UserDtos.AdminUnblockUserRequest("Resolved");
        when(service.unblockUser("usr_1", unblockReq)).thenReturn(new UserDtos.AdminUnblockUserResponse("usr_1", "ACTIVE"));

        UserDtos.AdminDeleteUserRequest delReq = new UserDtos.AdminDeleteUserRequest("Request", "DEACTIVATE");
        when(service.deleteUserAdmin("usr_1", delReq)).thenReturn(new UserDtos.AdminDeleteUserResponse("DEACTIVATED", null));

        assertThat(controller.listUsers(0, 10, null, null, null, null, null, null)).isEqualTo(listResponse);
        assertThat(controller.get("usr_1")).isEqualTo(detail);
        assertThat(controller.patchUser("usr_1", patchReq, "0")).isEqualTo(item);
        assertThat(controller.block("usr_1", blockReq, "idemp_1")).isEqualTo(new UserDtos.AdminBlockUserResponse("usr_1", "BLOCKED", 1));
        assertThat(controller.unblock("usr_1", unblockReq)).isEqualTo(new UserDtos.AdminUnblockUserResponse("usr_1", "ACTIVE"));

        ResponseEntity<UserDtos.AdminDeleteUserResponse> delRes = controller.delete("usr_1", delReq);
        assertThat(delRes.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(delRes.getBody()).isEqualTo(new UserDtos.AdminDeleteUserResponse("DEACTIVATED", null));
    }
}
