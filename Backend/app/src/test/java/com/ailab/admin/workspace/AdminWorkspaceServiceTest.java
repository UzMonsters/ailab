package com.ailab.admin.workspace;

import com.ailab.user.domain.Role;
import com.ailab.user.domain.User;
import com.ailab.user.repository.UserRepository;
import com.ailab.workspace.domain.WorkspaceEntity;
import com.ailab.workspace.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

    @InjectMocks
    private AdminWorkspaceService service;

    @Test
    void listCombinesWorkspacePageWithGroupedAggregateCounts() {
        User owner = new User("owner", "owner@example.com", "hash", Role.USER);
        WorkspaceEntity workspace = new WorkspaceEntity(
                "ws-1", owner.getId(), "Titration Lab", "chemistry", null);
        Instant updatedAt = Instant.parse("2026-09-15T10:15:30Z");
        workspace.setUpdatedAt(updatedAt);

        when(workspaceRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(workspace), PageRequest.of(0, 20), 1));
        when(memberRepository.countByWorkspaceIds(List.of("ws-1")))
                .thenReturn(List.<Object[]>of(new Object[]{"ws-1", 3L}));
        when(shareLinkRepository.countActiveByWorkspaceIds(eq(List.of("ws-1")), any(Instant.class)))
                .thenReturn(List.<Object[]>of(new Object[]{"ws-1", 2L}));
        when(invitationRepository.countPendingByWorkspaceIds(List.of("ws-1")))
                .thenReturn(List.<Object[]>of(new Object[]{"ws-1", 1L}));
        when(userRepository.findAllById(any())).thenReturn(List.of(owner));

        AdminWorkspaceDtos.PageDto result = service.list(
                "titration", "chemistry", "ACTIVE", owner.getId(), true, 0, 20, null);

        assertThat(result.items()).hasSize(1);
        AdminWorkspaceDtos.SummaryDto item = result.items().get(0);
        assertThat(item.id()).isEqualTo("ws-1");
        assertThat(item.memberCount()).isEqualTo(3);
        assertThat(item.activeShareLinkCount()).isEqualTo(2);
        assertThat(item.pendingInvitationCount()).isEqualTo(1);
        assertThat(item.status()).isEqualTo("ACTIVE");
        assertThat(item.owner().id()).isEqualTo(owner.getId());
        assertThat(item.updatedAt()).isEqualTo(updatedAt);
    }
}
