package com.ailab.admin.settings;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminSettingsControllerTest {

    @Mock
    AdminSettingsService service;

    @InjectMocks
    AdminSettingsController controller;

    @Test
    void testSettingsController() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("admin-1", "pass");

        when(service.getSettings()).thenReturn(Map.of("version", 1L));
        when(service.patchSettings(anyMap(), eq("1"), eq("admin-1"), anyString())).thenReturn(Map.of("version", 2L));
        when(service.getSchema("ru")).thenReturn(Map.of("groups", List.of()));
        when(service.getHistory(0, 20, null, null, null)).thenReturn(Map.of("items", List.of()));
        when(service.restoreVersion(eq(1L), anyString(), eq("admin-1"), anyString())).thenReturn(Map.of("version", 2L));
        when(service.getSubjects()).thenReturn(List.of(Map.of("id", "chemistry")));
        when(service.patchSubject(eq("chemistry"), anyMap(), eq("admin-1"), anyString())).thenReturn(Map.of("id", "chemistry", "enabled", true));

        assertThat(controller.getSettings()).isEqualTo(Map.of("version", 1L));
        assertThat(controller.patchSettings(Map.of(), "1", auth)).isEqualTo(Map.of("version", 2L));
        assertThat(controller.getSchema("ru")).isEqualTo(Map.of("groups", List.of()));
        assertThat(controller.getHistory(0, 20, null, null, null)).isEqualTo(Map.of("items", List.of()));

        ResponseEntity<Map<String, Object>> restoreRes = controller.restoreVersion(1L, Map.of("reason", "Rollback"), auth);
        assertThat(restoreRes.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(restoreRes.getBody()).isEqualTo(Map.of("version", 2L));

        assertThat(controller.getSubjects()).isEqualTo(Map.of("items", List.of(Map.of("id", "chemistry"))));
        assertThat(controller.patchSubject("chemistry", Map.of("enabled", true), auth)).isEqualTo(Map.of("id", "chemistry", "enabled", true));
    }
}
