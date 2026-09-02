package com.ailab.user.service;

import com.ailab.user.api.UserDtos;
import com.ailab.user.domain.Role;
import com.ailab.user.domain.User;
import com.ailab.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAccountServiceImplAdminTest {

    @Mock
    UserRepository repository;

    @Mock
    ApplicationEventPublisher events;

    @InjectMocks
    UserAccountServiceImpl service;

    @Test
    void testGetUsersPaged() {
        User user = new User("alice", "alice@example.com", "hash", Role.USER);
        Page<User> page = new PageImpl<>(List.of(user));
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(repository.countByRole(Role.USER)).thenReturn(1L);
        when(repository.countByRole(Role.ADMIN)).thenReturn(0L);
        when(repository.countByStatus("ACTIVE")).thenReturn(1L);
        when(repository.countByStatus("BLOCKED")).thenReturn(0L);
        when(repository.countByStatus("DEACTIVATED")).thenReturn(0L);

        UserDtos.AdminUserListResponse res = service.getUsersPaged(0, 10, null, null, null, null, null, null);

        assertThat(res.items()).hasSize(1);
        assertThat(res.items().get(0).displayName()).isEqualTo("alice");
        assertThat(res.page().totalElements()).isEqualTo(1);
    }

    @Test
    void testGetAdminUserDetail() {
        User user = new User("alice", "alice@example.com", "hash", Role.USER);
        when(repository.findById(user.getId())).thenReturn(Optional.of(user));

        UserDtos.AdminUserDetailResponse res = service.getAdminUserDetail(user.getId());

        assertThat(res.user().get("username")).isEqualTo("alice");
        assertThat(res.roles()).contains(Role.USER);
        assertThat(res.status()).isEqualTo("ACTIVE");
    }

    @Test
    void testPatchAdminUserSuccess() {
        User user = new User("alice", "alice@example.com", "hash", Role.USER);
        when(repository.findById(user.getId())).thenReturn(Optional.of(user));

        UserDtos.AdminPatchUserRequest req = new UserDtos.AdminPatchUserRequest("alice-updated", List.of(Role.ADMIN), Role.ADMIN, "ACTIVE", "Promotion");
        UserDtos.AdminUserListItem res = service.patchAdminUser(user.getId(), req, "0");

        assertThat(res.displayName()).isEqualTo("alice-updated");
        assertThat(res.role()).isEqualTo(Role.ADMIN);
    }

    @Test
    void testPatchAdminUserVersionConflict() {
        User user = new User("alice", "alice@example.com", "hash", Role.USER);
        when(repository.findById(user.getId())).thenReturn(Optional.of(user));

        UserDtos.AdminPatchUserRequest req = new UserDtos.AdminPatchUserRequest("alice-updated", List.of(Role.ADMIN), Role.ADMIN, "ACTIVE", "Promotion");

        assertThatThrownBy(() -> service.patchAdminUser(user.getId(), req, "99"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("VERSION_CONFLICT");
    }

    @Test
    void testPatchAdminUserMissingReason() {
        User user = new User("alice", "alice@example.com", "hash", Role.USER);
        when(repository.findById(user.getId())).thenReturn(Optional.of(user));

        UserDtos.AdminPatchUserRequest req = new UserDtos.AdminPatchUserRequest(null, List.of(Role.ADMIN), Role.ADMIN, "ACTIVE", null);

        assertThatThrownBy(() -> service.patchAdminUser(user.getId(), req, "0"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("VALIDATION_ERROR");
    }

    @Test
    void testBlockAndUnblockUser() {
        User user = new User("alice", "alice@example.com", "hash", Role.USER);
        when(repository.findById(user.getId())).thenReturn(Optional.of(user));

        UserDtos.AdminBlockUserRequest blockReq = new UserDtos.AdminBlockUserRequest("Spamming", null);
        UserDtos.AdminBlockUserResponse blockRes = service.blockUser(user.getId(), blockReq, "idemp-1");
        assertThat(blockRes.status()).isEqualTo("BLOCKED");

        UserDtos.AdminUnblockUserRequest unblockReq = new UserDtos.AdminUnblockUserRequest("Reviewed");
        UserDtos.AdminUnblockUserResponse unblockRes = service.unblockUser(user.getId(), unblockReq);
        assertThat(unblockRes.status()).isEqualTo("ACTIVE");
    }

    @Test
    void testDeleteUserAdminDeactivate() {
        User user = new User("alice", "alice@example.com", "hash", Role.USER);
        when(repository.findById(user.getId())).thenReturn(Optional.of(user));

        UserDtos.AdminDeleteUserRequest delReq = new UserDtos.AdminDeleteUserRequest("Deactivate account", "DEACTIVATE");
        UserDtos.AdminDeleteUserResponse delRes = service.deleteUserAdmin(user.getId(), delReq);

        assertThat(delRes.status()).isEqualTo("DEACTIVATED");
        assertThat(delRes.deletionScheduledFor()).isNull();
    }

    @Test
    void testDeleteUserAdminScheduled() {
        User user = new User("alice", "alice@example.com", "hash", Role.USER);
        when(repository.findById(user.getId())).thenReturn(Optional.of(user));

        UserDtos.AdminDeleteUserRequest delReq = new UserDtos.AdminDeleteUserRequest("GDPR Request", "SCHEDULE_DELETE");
        UserDtos.AdminDeleteUserResponse delRes = service.deleteUserAdmin(user.getId(), delReq);

        assertThat(delRes.status()).isEqualTo("DELETION_SCHEDULED");
        assertThat(delRes.deletionScheduledFor()).isNotNull();
    }
}
