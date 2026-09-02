package com.ailab.admin.audit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {

    @Mock
    AdminAuditRepository repository;

    @InjectMocks
    AuditLogServiceImpl service;

    @Test
    void testLogEvent() {
        AdminAuditEventEntity entity = new AdminAuditEventEntity(
                "usr_1", "Admin", "ADMIN", "setting.changed", "SYSTEM_SETTINGS", "global", "Settings",
                "SETTINGS", "ADMIN_WEB", "SUCCESS", "MEDIUM", null, null, List.of("app.name"),
                "req_1", "127.0.0.1", "curl", Map.of()
        );
        when(repository.save(any(AdminAuditEventEntity.class))).thenReturn(entity);

        AdminAuditEventEntity saved = service.logEvent(
                "usr_1", "Admin", "ADMIN", "setting.changed", "SYSTEM_SETTINGS", "global", "Settings",
                "SETTINGS", "ADMIN_WEB", "SUCCESS", "MEDIUM", null, null, List.of("app.name"),
                "req_1", "127.0.0.1", "curl", Map.of()
        );

        assertThat(saved.getAction()).isEqualTo("setting.changed");
        verify(repository).save(any(AdminAuditEventEntity.class));
    }

    @Test
    void testGetAuditEvents() {
        AdminAuditEventEntity entity = new AdminAuditEventEntity(
                "usr_1", "Admin", "ADMIN", "user.blocked", "USER", "usr_2", "User 2",
                "USERS", "ADMIN_WEB", "SUCCESS", "HIGH", null, null, List.of("status"),
                "req_1", "127.0.0.1", "curl", Map.of()
        );
        Page<AdminAuditEventEntity> page = new PageImpl<>(List.of(entity));
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(repository.findDistinctActions()).thenReturn(List.of("user.blocked"));
        when(repository.findDistinctSeverities()).thenReturn(List.of("HIGH"));
        when(repository.findDistinctSources()).thenReturn(List.of("ADMIN_WEB"));

        Map<String, Object> result = service.getAuditEvents(0, 10, null, null, null, null, null, null, null, null, null, null, null, null);

        assertThat(result.get("items")).isInstanceOf(List.class);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> items = (List<Map<String, Object>>) result.get("items");
        assertThat(items).hasSize(1);
        assertThat(items.get(0).get("action")).isEqualTo("user.blocked");
    }

    @Test
    void testGetAuditEventById() {
        AdminAuditEventEntity entity = new AdminAuditEventEntity(
                "usr_1", "Admin", "ADMIN", "user.blocked", "USER", "usr_2", "User 2",
                "USERS", "ADMIN_WEB", "SUCCESS", "HIGH", null, null, List.of("status"),
                "req_1", "127.0.0.1", "curl", Map.of()
        );
        when(repository.findById(entity.getId())).thenReturn(Optional.of(entity));

        Map<String, Object> res = service.getAuditEventById(entity.getId());
        assertThat(res.get("id")).isEqualTo(entity.getId());
        assertThat(res.get("action")).isEqualTo("user.blocked");
    }

    @Test
    void testExportJobAndRetention() {
        Map<String, Object> job = service.createExportJob("CSV", Map.of());
        assertThat(job.get("status")).isEqualTo("QUEUED");

        String jobId = String.valueOf(job.get("jobId"));
        Map<String, Object> status = service.getExportJob(jobId);
        assertThat(status.get("status")).isEqualTo("READY");

        Map<String, Object> retention = service.getRetentionPolicy();
        assertThat(retention.get("retentionDays")).isEqualTo(365);
        assertThat(retention.get("immutable")).isEqualTo(true);
    }
}
