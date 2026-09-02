package com.ailab.admin.audit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminAuditControllerTest {

    @Mock
    AuditLogService service;

    @InjectMocks
    AdminAuditController controller;

    @Test
    void testControllerEndpoints() {
        when(service.getAuditEvents(0, 50, null, null, null, null, null, null, null, null, null, null, null, null))
                .thenReturn(Map.of("items", List.of()));
        when(service.getAuditEventById("aud_1")).thenReturn(Map.of("id", "aud_1"));
        when(service.createExportJob("CSV", Map.of())).thenReturn(Map.of("jobId", "job_1", "status", "QUEUED"));
        when(service.getExportJob("job_1")).thenReturn(Map.of("status", "READY"));
        when(service.getRetentionPolicy()).thenReturn(Map.of("retentionDays", 365));

        assertThat(controller.listAuditEvents(0, 50, null, null, null, null, null, null, null, null, null, null, null, null))
                .isEqualTo(Map.of("items", List.of()));
        assertThat(controller.getAuditEventDetail("aud_1")).isEqualTo(Map.of("id", "aud_1"));

        ResponseEntity<Map<String, Object>> exportRes = controller.createAuditExport(Map.of("format", "CSV", "filters", Map.of()));
        assertThat(exportRes.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(exportRes.getBody()).isEqualTo(Map.of("jobId", "job_1", "status", "QUEUED"));

        assertThat(controller.getAuditExportStatus("job_1")).isEqualTo(Map.of("status", "READY"));
        assertThat(controller.getAuditRetention()).isEqualTo(Map.of("retentionDays", 365));
    }
}
