package com.ailab.admin.laboratory;

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
class AdminLaboratorySessionControllerTest {

    @Mock
    AdminLaboratoryMonitoringService service;

    @InjectMocks
    AdminLaboratorySessionController controller;

    @Test
    void testLaboratorySessionEndpoints() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("admin-1", "pass");

        when(service.getSessions(0, 20, null, null, null, null, null)).thenReturn(Map.of("items", List.of()));
        when(service.getSessionDetails("ws-1")).thenReturn(Map.of("session", Map.of()));
        when(service.pauseSession(eq("ws-1"), anyString(), eq("admin-1"), anyString())).thenReturn(Map.of("status", "PAUSED"));
        when(service.terminateSession(eq("ws-1"), anyString(), eq(true), eq("admin-1"), anyString())).thenReturn(Map.of("status", "TERMINATING"));

        assertThat(controller.listSessions(0, 20, null, null, null, null, null)).isEqualTo(Map.of("items", List.of()));
        assertThat(controller.getSessionDetail("ws-1")).isEqualTo(Map.of("session", Map.of()));
        assertThat(controller.pauseSession("ws-1", Map.of("reason", "Maintenance"), auth)).isEqualTo(Map.of("status", "PAUSED"));

        ResponseEntity<Map<String, Object>> termRes = controller.terminateSession("ws-1", Map.of("reason", "Incident", "notifyOwner", true), auth);
        assertThat(termRes.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(termRes.getBody()).isEqualTo(Map.of("status", "TERMINATING"));
    }
}
