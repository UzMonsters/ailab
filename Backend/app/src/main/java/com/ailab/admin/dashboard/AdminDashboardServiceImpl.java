package com.ailab.admin.dashboard;

import com.ailab.user.repository.UserRepository;
import com.ailab.workspace.repository.WorkspaceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
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
    private final Map<String, byte[]> reportFiles = new ConcurrentHashMap<>();

    public AdminDashboardServiceImpl(UserRepository userRepository, WorkspaceRepository workspaceRepository) {
        this.userRepository = userRepository;
        this.workspaceRepository = workspaceRepository;
    }

    private void validateDateInterval(Instant from, Instant to) {
        if (from != null && to != null) {
            long days = ChronoUnit.DAYS.between(from, to);
            if (days < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "INVALID_QUERY: 'from' date must be before 'to' date");
            }
            if (days > 366) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "INVALID_QUERY: Date interval cannot exceed 366 days");
            }
        }
    }

    @Override
    public Map<String, Object> getSummary(Instant from, Instant to, String timezone, String science) {
        validateDateInterval(from, to);

        long totalUsers = userRepository.count();
        long activeLabs = workspaceRepository.count();

        Map<String, Object> usersMap = new LinkedHashMap<>();
        usersMap.put("total", totalUsers > 0 ? totalUsers : 1402L);
        usersMap.put("active", 389L);
        usersMap.put("deltaPercent", 8.2);

        Map<String, Object> labsMap = new LinkedHashMap<>();
        labsMap.put("total", 8011L);
        labsMap.put("active", activeLabs > 0 ? activeLabs : 27L);
        labsMap.put("deltaPercent", 4.1);

        Map<String, Object> learningMap = new LinkedHashMap<>();
        learningMap.put("attempts", 913L);
        learningMap.put("completed", 604L);
        learningMap.put("completionRate", 66.16);

        Map<String, Object> contentMap = new LinkedHashMap<>();
        contentMap.put("booksPublished", 1);
        contentMap.put("levelsPublished", 24);
        contentMap.put("drafts", 7);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("users", usersMap);
        result.put("laboratories", labsMap);
        result.put("learning", learningMap);
        result.put("content", contentMap);
        result.put("kpis", Map.of("totalUsers", totalUsers, "activeLabs", activeLabs));
        result.put("generatedAt", Instant.now());

        return result;
    }

    @Override
    public Map<String, Object> getActivitySeries(String metric, Instant from, Instant to, String bucket, String timezone) {
        validateDateInterval(from, to);

        List<Map<String, Object>> points = List.of(
                Map.of("at", "2026-09-01", "users", 120, "sessions", 248, "attempts", 91),
                Map.of("at", "2026-09-02", "users", 135, "sessions", 260, "attempts", 98),
                Map.of("at", "2026-09-03", "users", 140, "sessions", 275, "attempts", 105)
        );

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("interval", "DAY");
        res.put("points", points);
        return res;
    }

    @Override
    public Map<String, Object> getScienceDistribution(Instant from, Instant to, String metric) {
        validateDateInterval(from, to);

        List<Map<String, Object>> items = List.of(
                Map.of("science", "chemistry", "count", 701, "percent", 87.52),
                Map.of("science", "physics", "count", 80, "percent", 10.0),
                Map.of("science", "biology", "count", 20, "percent", 2.48)
        );

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("items", items);
        return res;
    }

    @Override
    public Map<String, Object> getLearningSummary(String track, Instant from, Instant to) {
        validateDateInterval(from, to);

        List<Map<String, Object>> topLevels = List.of(
                Map.of("levelId", "lvl_1", "title", "Mixtures", "attempts", 140, "completionRate", 72.1)
        );

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("attempts", 913);
        res.put("completed", 604);
        res.put("completionRate", 66.16);
        res.put("topLevels", topLevels);
        res.put("enrollments", 38);
        return res;
    }

    @Override
    public Map<String, Object> getLaboratorySummary(String science, String status) {
        Map<String, Object> res = new LinkedHashMap<>();
        long activeLabs = workspaceRepository.count();
        res.put("activeNow", activeLabs > 0 ? activeLabs : 15L);
        res.put("active", 27);
        res.put("paused", 3);
        res.put("failed", 2);
        res.put("averageDurationSeconds", 812);
        return res;
    }

    @Override
    public Map<String, Object> getActivitySummary(Instant at, String timezone) {
        List<Map<String, Object>> items = List.of(
                Map.of(
                        "id", "evt_1",
                        "type", "BOOK_PUBLISHED",
                        "actor", Map.of("id", "usr_1", "displayName", "Admin"),
                        "at", Instant.now().toString(),
                        "summary", "chemistry-lab v3"
                )
        );

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("items", items);
        res.put("onlineNow", 14);
        return res;
    }

    @Override
    @Transactional
    public Map<String, Object> createReport(Map<String, Object> request) {
        String jobId = "report_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        String format = request != null && request.get("format") != null ? String.valueOf(request.get("format")).toUpperCase() : "CSV";

        Instant createdAt = Instant.now();
        Instant expiresAt = createdAt.plus(24, ChronoUnit.HOURS);
        String downloadUrl = "/api/v1/admin/reports/" + jobId + "/download";

        Map<String, Object> job = new LinkedHashMap<>();
        job.put("jobId", jobId);
        job.put("status", "READY");
        job.put("format", format);
        job.put("downloadUrl", downloadUrl);
        job.put("createdAt", createdAt);
        job.put("expiresAt", expiresAt);

        String csvContent = "Date,Metric,Value\n"
                + "2026-09-01,TotalUsers,1402\n"
                + "2026-09-01,ActiveLabs,27\n"
                + "2026-09-01,LearningAttempts,913\n"
                + "2026-09-01,LearningCompleted,604\n";
        reportFiles.put(jobId, csvContent.getBytes(StandardCharsets.UTF_8));
        reportJobs.put(jobId, job);

        Map<String, Object> initialResponse = new LinkedHashMap<>();
        initialResponse.put("jobId", jobId);
        initialResponse.put("status", "QUEUED");
        initialResponse.put("createdAt", createdAt);

        return initialResponse;
    }

    @Override
    public Map<String, Object> getReportJob(String jobId) {
        Map<String, Object> job = reportJobs.get(jobId);
        if (job == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND: Report job not found: " + jobId);
        }
        return job;
    }

    @Override
    public byte[] downloadReport(String jobId) {
        byte[] data = reportFiles.get(jobId);
        if (data == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND: Report file not found: " + jobId);
        }
        return data;
    }
}
