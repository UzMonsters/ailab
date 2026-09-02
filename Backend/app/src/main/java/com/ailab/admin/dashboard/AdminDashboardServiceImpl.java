package com.ailab.admin.dashboard;

import com.ailab.user.repository.UserRepository;
import com.ailab.workspace.repository.WorkspaceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional(readOnly = true)
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;
    private final Map<String, Map<String, Object>> reportJobs = new ConcurrentHashMap<>();

    public AdminDashboardServiceImpl(UserRepository userRepository, WorkspaceRepository workspaceRepository) {
        this.userRepository = userRepository;
        this.workspaceRepository = workspaceRepository;
    }

    @Override
    public Map<String, Object> getSummary(Instant from, Instant to, String timezone, String science) {
        long totalUsers = userRepository.count();
        long activeLabs = workspaceRepository.count();

        Map<String, Object> kpis = Map.of(
                "totalUsers", totalUsers,
                "activeLabs", activeLabs,
                "experiments", 142L,
                "averageScore", 86.4,
                "safetyIncidents", 2L
        );

        Map<String, Object> comparison = Map.of(
                "totalUsersDelta", "+12.5%",
                "activeLabsDelta", "+8.3%",
                "experimentsDelta", "+15.0%",
                "averageScoreDelta", "+2.1%"
        );

        return Map.of(
                "period", Map.of(
                        "from", from != null ? from : Instant.now().minus(30, ChronoUnit.DAYS),
                        "to", to != null ? to : Instant.now(),
                        "timezone", timezone != null ? timezone : "UTC"
                ),
                "kpis", kpis,
                "comparison", comparison
        );
    }

    @Override
    public Map<String, Object> getActivitySeries(String metric, Instant from, Instant to, String bucket, String timezone) {
        String metricName = metric != null ? metric.toLowerCase() : "experiments";
        String unit = "count";
        List<Map<String, Object>> points = new ArrayList<>();

        Instant start = from != null ? from : Instant.now().minus(7, ChronoUnit.DAYS);
        Instant end = to != null ? to : Instant.now();

        long stepHours = "hour".equalsIgnoreCase(bucket) ? 1 : "week".equalsIgnoreCase(bucket) ? 168 : 24;
        Instant current = start;
        int seed = 10;
        while (!current.isAfter(end)) {
            points.add(Map.of(
                    "at", current,
                    "value", (seed * 7 + points.size() * 3) % 45 + 5
            ));
            current = current.plus(stepHours, ChronoUnit.HOURS);
        }

        return Map.of(
                "metric", metricName,
                "unit", unit,
                "points", points
        );
    }

    @Override
    public Map<String, Object> getScienceDistribution(Instant from, Instant to, String metric) {
        List<Map<String, Object>> items = List.of(
                Map.of("science", "Chemistry", "count", 85, "percentage", 75.0),
                Map.of("science", "Physics", "count", 25, "percentage", 20.0),
                Map.of("science", "Biology", "count", 5, "percentage", 5.0)
        );

        return Map.of(
                "total", 115,
                "items", items
        );
    }

    @Override
    public Map<String, Object> getLearningSummary(String track, Instant from, Instant to) {
        List<Map<String, Object>> recent = List.of(
                Map.of(
                        "userId", "usr_01",
                        "username", "Jasur Karimov",
                        "levelId", "chem_acid_base_1",
                        "levelName", "Acid-Base Titration Basics",
                        "score", 95,
                        "completedAt", Instant.now().minus(2, ChronoUnit.HOURS)
                )
        );

        return Map.of(
                "enrollments", 38,
                "averageCompletionSeconds", 420,
                "successRate", 91.2,
                "recent", recent
        );
    }

    @Override
    public Map<String, Object> getLaboratorySummary(String science, String status) {
        long count = workspaceRepository.count();
        return Map.of(
                "activeNow", count,
                "byScience", Map.of("chemistry", count, "physics", 0L, "biology", 0L),
                "byStatus", Map.of("active", count, "paused", 0L, "terminated", 0L)
        );
    }

    @Override
    public Map<String, Object> getActivitySummary(Instant at, String timezone) {
        return Map.of(
                "onlineNow", 14,
                "experimentsToday", 48,
                "lessonsCompleted", 32,
                "averageSessionSeconds", 1250
        );
    }

    @Override
    @Transactional
    public Map<String, Object> createReport(Map<String, Object> request) {
        String jobId = "rep_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        String format = request.get("format") != null ? String.valueOf(request.get("format")).toUpperCase() : "CSV";

        Map<String, Object> job = new LinkedHashMap<>();
        job.put("jobId", jobId);
        job.put("status", "READY");
        job.put("format", format);
        job.put("downloadUrl", "/api/v1/admin/reports/" + jobId + "/download");
        job.put("expiresAt", Instant.now().plus(24, ChronoUnit.HOURS));
        job.put("createdAt", Instant.now());

        reportJobs.put(jobId, job);

        return Map.of(
                "jobId", jobId,
                "status", "QUEUED"
        );
    }

    @Override
    public Map<String, Object> getReportJob(String jobId) {
        Map<String, Object> job = reportJobs.get(jobId);
        if (job == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Report job not found: " + jobId);
        }
        return job;
    }
}
