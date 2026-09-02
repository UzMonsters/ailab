package com.ailab.admin.laboratory;

import com.ailab.admin.audit.AuditLogService;
import com.ailab.workspace.domain.WorkspaceEntity;
import com.ailab.workspace.repository.WorkspaceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminLaboratoryMonitoringServiceTest {

    @Mock
    WorkspaceRepository workspaceRepository;

    @Mock
    AuditLogService auditLogService;

    @InjectMocks
    AdminLaboratoryMonitoringServiceImpl service;

    @Test
    void testGetSessionsAndDetails() {
        WorkspaceEntity workspace = new WorkspaceEntity("ws-1", "usr-1", "Chem Lab", "chemistry", "sess-1");
        when(workspaceRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(workspace)));
        when(workspaceRepository.findById("ws-1")).thenReturn(Optional.of(workspace));

        Map<String, Object> sessions = service.getSessions(0, 20, null, null, null, null, null);
        assertThat(sessions.get("items")).isInstanceOf(List.class);

        Map<String, Object> details = service.getSessionDetails("ws-1");
        assertThat(details.get("session")).isInstanceOf(Map.class);
    }

    @Test
    void testPauseAndTerminateSession() {
        Map<String, Object> paused = service.pauseSession("ws-1", "Safety alert", "admin-1", "Admin");
        assertThat(paused.get("status")).isEqualTo("PAUSED");

        Map<String, Object> terminated = service.terminateSession("ws-1", "Critical issue", true, "admin-1", "Admin");
        assertThat(terminated.get("status")).isEqualTo("TERMINATING");
    }

    @Test
    void testPauseRequiresReason() {
        assertThatThrownBy(() -> service.pauseSession("ws-1", null, "admin-1", "Admin"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("VALIDATION_ERROR");
    }
}
