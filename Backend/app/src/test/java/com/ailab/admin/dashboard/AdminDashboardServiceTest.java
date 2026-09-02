package com.ailab.admin.dashboard;

import com.ailab.user.repository.UserRepository;
import com.ailab.workspace.repository.WorkspaceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminDashboardServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    WorkspaceRepository workspaceRepository;

    @InjectMocks
    AdminDashboardServiceImpl service;

    @Test
    void testDashboardAggregations() {
        when(userRepository.count()).thenReturn(100L);
        when(workspaceRepository.count()).thenReturn(15L);

        Map<String, Object> summary = service.getSummary(null, null, "UTC", "chemistry");
        assertThat(summary.get("kpis")).isInstanceOf(Map.class);
        @SuppressWarnings("unchecked")
        Map<String, Object> kpis = (Map<String, Object>) summary.get("kpis");
        assertThat(kpis.get("totalUsers")).isEqualTo(100L);
        assertThat(kpis.get("activeLabs")).isEqualTo(15L);

        Map<String, Object> series = service.getActivitySeries("experiments", null, null, "day", "UTC");
        assertThat(series.get("points")).isInstanceOf(List.class);

        Map<String, Object> dist = service.getScienceDistribution(null, null, "labs");
        assertThat(dist.get("items")).isInstanceOf(List.class);

        Map<String, Object> learning = service.getLearningSummary(null, null, null);
        assertThat(learning.get("enrollments")).isEqualTo(38);

        Map<String, Object> labSummary = service.getLaboratorySummary(null, null);
        assertThat(labSummary.get("activeNow")).isEqualTo(15L);

        Map<String, Object> actSummary = service.getActivitySummary(null, "UTC");
        assertThat(actSummary.get("onlineNow")).isEqualTo(14);

        Map<String, Object> report = service.createReport(Map.of("format", "CSV"));
        assertThat(report.get("status")).isEqualTo("QUEUED");

        Map<String, Object> job = service.getReportJob(String.valueOf(report.get("jobId")));
        assertThat(job.get("status")).isEqualTo("READY");
    }
}
