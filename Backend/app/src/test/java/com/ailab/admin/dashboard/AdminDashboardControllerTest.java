package com.ailab.admin.dashboard;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminDashboardControllerTest {

    @Mock
    AdminDashboardService service;

    @InjectMocks
    AdminDashboardController controller;

    @Test
    void testDashboardEndpoints() {
        when(service.getSummary(any(), any(), any(), any())).thenReturn(Map.of("kpis", Map.of()));
        when(service.getActivitySeries(anyString(), any(), any(), anyString(), any())).thenReturn(Map.of("metric", "experiments"));
        when(service.getScienceDistribution(any(), any(), anyString())).thenReturn(Map.of("total", 100));
        when(service.getLearningSummary(any(), any(), any())).thenReturn(Map.of("enrollments", 20));
        when(service.getLaboratorySummary(any(), any())).thenReturn(Map.of("activeNow", 5));
        when(service.getActivitySummary(any(), any())).thenReturn(Map.of("onlineNow", 10));
        when(service.createReport(anyMap())).thenReturn(Map.of("jobId", "rep_1", "status", "QUEUED"));
        when(service.getReportJob("rep_1")).thenReturn(Map.of("status", "READY"));

        assertThat(controller.getSummary(null, null, null, null)).isEqualTo(Map.of("kpis", Map.of()));
        assertThat(controller.getActivitySeries("experiments", null, null, "day", null)).isEqualTo(Map.of("metric", "experiments"));
        assertThat(controller.getScienceDistribution(null, null, "labs")).isEqualTo(Map.of("total", 100));
        assertThat(controller.getLearningSummary(null, null, null)).isEqualTo(Map.of("enrollments", 20));
        assertThat(controller.getLaboratorySummary(null, null)).isEqualTo(Map.of("activeNow", 5));
        assertThat(controller.getActivitySummary(null, null)).isEqualTo(Map.of("onlineNow", 10));

        ResponseEntity<Map<String, Object>> repRes = controller.createReport(Map.of("format", "CSV"));
        assertThat(repRes.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(repRes.getBody()).isEqualTo(Map.of("jobId", "rep_1", "status", "QUEUED"));

        assertThat(controller.getReport("rep_1")).isEqualTo(Map.of("status", "READY"));
    }
}
