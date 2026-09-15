package com.ailab.admin.workspace;

import com.ailab.user.domain.User;
import com.ailab.user.repository.UserRepository;
import com.ailab.workspace.domain.WorkspaceEntity;
import com.ailab.workspace.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminWorkspaceServiceTest {

    @Mock
    private WorkspaceRepository workspaceRepository;
    @Mock
    private WorkspaceMemberRepository memberRepository;
    @Mock
    private WorkspaceInvitationRepository invitationRepository;
    @Mock
    private WorkspaceShareLinkRepository shareLinkRepository;
    @Mock
    private WorkspacePreviewRepository previewRepository;
    @Mock
    private WorkspaceEventRepository eventRepository;
    @Mock
    private WorkspaceStateRepository stateRepository;
    @Mock
    private WorkspaceChatMessageRepository chatMessageRepository;
    @Mock
    private WorkspaceChatReadRepository chatReadRepository;
    @Mock
    private WorkspaceCommentThreadRepository commentThreadRepository;
    @Mock
    private MeasurementRepository measurementRepository;
    @Mock
    private UserRepository userRepository;

    private AdminWorkspaceService service;

    @BeforeEach
    void setUp() {
        service = new AdminWorkspaceService(
                workspaceRepository,
                memberRepository,
                invitationRepository,
                shareLinkRepository,
                previewRepository,
                eventRepository,
                stateRepository,
                chatMessageRepository,
                chatReadRepository,
                commentThreadRepository,
                measurementRepository,
                userRepository
        );
    }

    @Test
    void testListWorkspacesWithAggregatedCounts() {
        WorkspaceEntity ws = new WorkspaceEntity("ws_1", "usr_1", "Chemistry Lab 101", "chemistry", "sess_1");
        ws.setStateVersion(4L);
        ws.setUpdatedAt(Instant.now());

        when(workspaceRepository.findAdminWorkspaces(anyString(), anyBoolean(), anyString(), anyBoolean(), anyString(), anyString(), anyBoolean(), any(), anyBoolean(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(ws)));

        when(memberRepository.countMembersByWorkspaceIds(Set.of("ws_1")))
                .thenReturn(List.<Object[]>of(new Object[]{"ws_1", 3L}));

        when(shareLinkRepository.countActiveShareLinksByWorkspaceIds(eq(Set.of("ws_1")), any(Instant.class)))
                .thenReturn(List.<Object[]>of(new Object[]{"ws_1", 2L}));

        when(invitationRepository.countPendingInvitationsByWorkspaceIds(Set.of("ws_1")))
                .thenReturn(List.<Object[]>of(new Object[]{"ws_1", 1L}));

        User owner = org.mockito.Mockito.mock(User.class);
        when(owner.getId()).thenReturn("usr_1");
        when(owner.getDisplayName()).thenReturn("Lab Admin");
        when(owner.getEmail()).thenReturn("admin@ailab.local");
        when(userRepository.findAllById(Set.of("usr_1"))).thenReturn(List.of(owner));


        AdminWorkspaceDtos.PageDto result = service.list("", "", "ACTIVE", "", null, 0, 10, "updatedAt,desc");

        assertThat(result).isNotNull();
        assertThat(result.items()).hasSize(1);
        AdminWorkspaceDtos.SummaryDto summary = result.items().get(0);
        assertThat(summary.id()).isEqualTo("ws_1");
        assertThat(summary.name()).isEqualTo("Chemistry Lab 101");
        assertThat(summary.science()).isEqualTo("chemistry");
        assertThat(summary.status()).isEqualTo("ACTIVE");
        assertThat(summary.memberCount()).isEqualTo(3L);
        assertThat(summary.activeShareLinkCount()).isEqualTo(2L);
        assertThat(summary.pendingInvitationCount()).isEqualTo(1L);
        assertThat(summary.owner().id()).isEqualTo("usr_1");
        assertThat(summary.owner().displayName()).isEqualTo("Lab Admin");
    }

    @Test
    void testListEmptyWorkspaces() {
        when(workspaceRepository.findAdminWorkspaces(anyString(), anyBoolean(), anyString(), anyBoolean(), anyString(), anyString(), anyBoolean(), any(), anyBoolean(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        AdminWorkspaceDtos.PageDto result = service.list("", "", "", "", null, 0, 10, "");

        assertThat(result).isNotNull();
        assertThat(result.items()).isEmpty();
        assertThat(result.page().totalElements()).isEqualTo(0L);
    }
}
