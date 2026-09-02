package com.ailab.admin.dashboard;

import java.time.Instant;
import java.util.Map;

public interface AdminDashboardService {

    Map<String, Object> getSummary(Instant from, Instant to, String timezone, String science);

    Map<String, Object> getActivitySeries(String metric, Instant from, Instant to, String bucket, String timezone);

    Map<String, Object> getScienceDistribution(Instant from, Instant to, String metric);

    Map<String, Object> getLearningSummary(String track, Instant from, Instant to);

    Map<String, Object> getLaboratorySummary(String science, String status);

    Map<String, Object> getActivitySummary(Instant at, String timezone);

    Map<String, Object> createReport(Map<String, Object> request);

    Map<String, Object> getReportJob(String jobId);
}
