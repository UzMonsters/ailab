package com.ailab.admin.settings;

import com.ailab.admin.audit.AuditLogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminSettingsServiceTest {

    @Mock
    AdminSettingsRepository settingsRepository;

    @Mock
    AdminSettingsHistoryRepository historyRepository;

    @Mock
    AdminSubjectRepository subjectRepository;

    @Mock
    AuditLogService auditLogService;

    @InjectMocks
    AdminSettingsServiceImpl service;

    @Test
    void testGetSettings() {
        AdminSettingsEntity entity = new AdminSettingsEntity(
                "global", Map.of("general", Map.of("appName", "jasScience")), 1L, "settings-v1",
                Instant.now(), "system", "System"
        );
        when(settingsRepository.findById("global")).thenReturn(Optional.of(entity));

        Map<String, Object> result = service.getSettings();
        assertThat(result.get("version")).isEqualTo(1L);
        assertThat(result.get("etag")).isEqualTo("settings-v1");
    }

    @Test
    void testPatchSettingsSuccess() {
        AdminSettingsEntity entity = new AdminSettingsEntity(
                "global", Map.of("general", Map.of("appName", "jasScience")), 1L, "settings-v1",
                Instant.now(), "system", "System"
        );
        when(settingsRepository.findById("global")).thenReturn(Optional.of(entity));

        Map<String, Object> patch = Map.of("general", Map.of("appName", "jasScience Updated"));
        Map<String, Object> result = service.patchSettings(patch, "1", "admin-1", "Admin");

        assertThat(result.get("version")).isEqualTo(2L);
        verify(settingsRepository).save(entity);
        verify(historyRepository).save(any(AdminSettingsHistoryEntity.class));
    }

    @Test
    void testPatchSettingsVersionConflict() {
        AdminSettingsEntity entity = new AdminSettingsEntity(
                "global", Map.of("general", Map.of("appName", "jasScience")), 5L, "settings-v5",
                Instant.now(), "system", "System"
        );
        when(settingsRepository.findById("global")).thenReturn(Optional.of(entity));

        Map<String, Object> patch = Map.of("general", Map.of("appName", "jasScience Updated"));

        assertThatThrownBy(() -> service.patchSettings(patch, "4", "admin-1", "Admin"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("VERSION_CONFLICT");
    }

    @Test
    void testGetSchema() {
        Map<String, Object> schema = service.getSchema("ru");
        assertThat(schema.get("groups")).isInstanceOf(List.class);
    }

    @Test
    void testRestoreVersion() {
        AdminSettingsEntity entity = new AdminSettingsEntity(
                "global", Map.of("general", Map.of("appName", "jasScience")), 3L, "settings-v3",
                Instant.now(), "system", "System"
        );
        when(settingsRepository.findById("global")).thenReturn(Optional.of(entity));

        AdminSettingsHistoryEntity history = new AdminSettingsHistoryEntity(
                1L, "system", "System", List.of("all"), Map.of("general", Map.of("appName", "Initial"))
        );
        when(historyRepository.findByVersion(1L)).thenReturn(Optional.of(history));

        Map<String, Object> res = service.restoreVersion(1L, "Rollback", "admin-1", "Admin");
        assertThat(res.get("version")).isEqualTo(4L);
        assertThat(res.get("restoredFrom")).isEqualTo(1L);
    }

    @Test
    void testSubjectsCRUD() {
        AdminSubjectEntity subject = new AdminSubjectEntity("chemistry", "Chemistry", true, "#3B82F6", 1);
        when(subjectRepository.findAllByOrderBySortOrderAsc()).thenReturn(List.of(subject));
        when(subjectRepository.findById("chemistry")).thenReturn(Optional.of(subject));

        List<Map<String, Object>> subjects = service.getSubjects();
        assertThat(subjects).hasSize(1);

        Map<String, Object> updated = service.patchSubject("chemistry", Map.of("enabled", false), "admin-1", "Admin");
        assertThat(updated.get("enabled")).isEqualTo(false);
    }
}
