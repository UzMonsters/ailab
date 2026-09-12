package com.ailab.admin.dashboard;

import com.ailab.admin.assets.AssetStorageService;
import com.ailab.admin.audit.AdminAuditEventEntity;
import com.ailab.admin.audit.AdminAuditRepository;
import com.ailab.admin.catalog.AdminCatalogDraftRepository;
import com.ailab.book.domain.BookStatus;
import com.ailab.book.repository.BookRepository;
import com.ailab.learning.domain.AttemptStatus;
import com.ailab.learning.domain.LearningStatus;
import com.ailab.learning.repository.LearningLevelRepository;
import com.ailab.learning.repository.LearningUserAttemptRepository;
import com.ailab.user.repository.UserRepository;
import com.ailab.workspace.repository.WorkspaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional(readOnly = true)
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;
    private final LearningUserAttemptRepository attemptRepository;
    private final LearningLevelRepository levelRepository;
    private final AdminCatalogDraftRepository catalogDraftRepository;
    private final AdminAuditRepository auditRepository;
    private final AdminExportJobRepository exportJobRepository;
    private final AssetStorageService assetStorageService;
    private final BookRepository bookRepository;

    private final Map<String, Map<String, Object>> inMemoryJobs = new ConcurrentHashMap<>();
    private final Map<String, byte[]> inMemoryFiles = new ConcurrentHashMap<>();

    public AdminDashboardServiceImpl(UserRepository userRepository, WorkspaceRepository workspaceRepository) {
        this(userRepository, workspaceRepository, null, null, null, null, null, null, null);
    }

    @Autowired
    public AdminDashboardServiceImpl(
            UserRepository userRepository,
            WorkspaceRepository workspaceRepository,
            @Autowired(required = false) LearningUserAttemptRepository attemptRepository,
            @Autowired(required = false) LearningLevelRepository levelRepository,
            @Autowired(required = false) AdminCatalogDraftRepository catalogDraftRepository,
            @Autowired(required = false) AdminAuditRepository auditRepository,
            @Autowired(required = false) AdminExportJobRepository exportJobRepository,
            @Autowired(required = false) AssetStorageService assetStorageService,
            @Autowired(required = false) BookRepository bookRepository
    ) {
        this.userRepository = userRepository;
        this.workspaceRepository = workspaceRepository;
        this.attemptRepository = attemptRepository;
        this.levelRepository = levelRepository;
        this.catalogDraftRepository = catalogDraftRepository;
        this.auditRepository = auditRepository;
        this.exportJobRepository = exportJobRepository;
        this.assetStorageService = assetStorageService;
        this.bookRepository = bookRepository;
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

        long totalUsers = userRepository != null ? userRepository.count() : 0L;
        long activeUsers = userRepository != null ? userRepository.countByStatus("ACTIVE") : 0L;

        long totalLabs = workspaceRepository != null ? workspaceRepository.count() : 0L;
        long activeLabs = workspaceRepository != null ? workspaceRepository.countByIsDeletedFalse() : 0L;

        long attempts = attemptRepository != null ? attemptRepository.count() : 0L;
        long completed = attemptRepository != null ? attemptRepository.countByStatus(AttemptStatus.COMPLETED) : 0L;
        double completionRate = attempts > 0 ? Math.round((completed * 10000.0) / attempts) / 100.0 : 0.0;

        long booksPublished = bookRepository != null ? bookRepository.countByStatus(BookStatus.PUBLISHED) : 0L;
        long levelsPublished = levelRepository != null ? levelRepository.countByStatus(LearningStatus.PUBLISHED) : 0L;
        long drafts = catalogDraftRepository != null ? catalogDraftRepository.countByStatus("DRAFT") : 0L;

        Map<String, Object> usersMap = new LinkedHashMap<>();
        usersMap.put("total", totalUsers);
        usersMap.put("active", activeUsers);
        usersMap.put("deltaPercent", 0.0);

        Map<String, Object> labsMap = new LinkedHashMap<>();
        labsMap.put("total", totalLabs);
        labsMap.put("active", activeLabs);
        labsMap.put("deltaPercent", 0.0);

        Map<String, Object> learningMap = new LinkedHashMap<>();
        learningMap.put("attempts", attempts);
        learningMap.put("completed", completed);
        learningMap.put("completionRate", completionRate);

        Map<String, Object> contentMap = new LinkedHashMap<>();
        contentMap.put("booksPublished", booksPublished);
        contentMap.put("levelsPublished", levelsPublished);
        contentMap.put("drafts", drafts);

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

        Instant end = (to != null) ? to : Instant.now();
        Instant start = (from != null) ? from : end.minus(7, ChronoUnit.DAYS);

        List<Map<String, Object>> points = new ArrayList<>();
        Instant cur = start;
        while (cur.isBefore(end)) {
            Instant next = cur.plus(1, ChronoUnit.DAYS);
            long count = 0L;
            if (auditRepository != null) {
                count = auditRepository.countByOccurredAtBetween(cur, next);
            }
            LocalDate localDate = cur.atZone(ZoneOffset.UTC).toLocalDate();
            points.add(Map.of(
                    "at", localDate.toString(),
                    "users", count,
                    "sessions", count,
                    "attempts", count
            ));
            cur = next;
        }

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("interval", bucket != null ? bucket.toUpperCase() : "DAY");
        res.put("points", points);
        return res;
    }

    @Override
    public Map<String, Object> getScienceDistribution(Instant from, Instant to, String metric) {
        validateDateInterval(from, to);

        List<Map<String, Object>> items = new ArrayList<>();
        if (workspaceRepository != null) {
            List<Object[]> rows = workspaceRepository.countWorkspacesByScience();
            long total = 0L;
            for (Object[] row : rows) {
                if (row.length > 1 && row[1] instanceof Number n) {
                    total += n.longValue();
                }
            }

            for (Object[] row : rows) {
                String science = row[0] != null ? String.valueOf(row[0]) : "unknown";
                long count = (row.length > 1 && row[1] instanceof Number n) ? n.longValue() : 0L;
                double pct = total > 0 ? Math.round((count * 10000.0) / total) / 100.0 : 0.0;
                items.add(Map.of(
                        "science", science,
                        "count", count,
                        "percent", pct
                ));
            }
        }

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("items", items);
        return res;
    }

    @Override
    public Map<String, Object> getLearningSummary(String track, Instant from, Instant to) {
        validateDateInterval(from, to);

        long attempts = attemptRepository != null ? attemptRepository.count() : 0L;
        long completed = attemptRepository != null ? attemptRepository.countByStatus(AttemptStatus.COMPLETED) : 0L;
        double completionRate = attempts > 0 ? Math.round((completed * 10000.0) / attempts) / 100.0 : 0.0;

        List<Map<String, Object>> topLevels = List.of();

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("attempts", attempts);
        res.put("completed", completed);
        res.put("completionRate", completionRate);
        res.put("topLevels", topLevels);
        res.put("enrollments", 0);
        return res;
    }

    @Override
    public Map<String, Object> getLaboratorySummary(String science, String status) {
        long activeLabs = workspaceRepository != null ? workspaceRepository.countByIsDeletedFalse() : 0L;

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("activeNow", activeLabs);
        res.put("active", activeLabs);
        res.put("paused", 0);
        res.put("failed", 0);
        res.put("averageDurationSeconds", 0);
        return res;
    }

    @Override
    public Map<String, Object> getActivitySummary(Instant at, String timezone) {
        List<Map<String, Object>> items = new ArrayList<>();
        if (auditRepository != null) {
            List<AdminAuditEventEntity> events = auditRepository.findTop10ByOrderByOccurredAtDesc();
            for (AdminAuditEventEntity e : events) {
                items.add(Map.of(
                        "id", e.getId(),
                        "type", e.getAction(),
                        "actor", Map.of("id", e.getActorId(), "displayName", e.getActorName()),
                        "at", e.getOccurredAt().toString(),
                        "summary", e.getEntityLabel() != null ? e.getEntityLabel() : e.getAction()
                ));
            }
        }

        long onlineNow = userRepository != null ? userRepository.countByStatus("ACTIVE") : 0L;

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("items", items);
        res.put("onlineNow", onlineNow);
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

        long totalUsers = userRepository != null ? userRepository.count() : 0L;
        long activeLabs = workspaceRepository != null ? workspaceRepository.countByIsDeletedFalse() : 0L;
        long attempts = attemptRepository != null ? attemptRepository.count() : 0L;
        long completed = attemptRepository != null ? attemptRepository.countByStatus(AttemptStatus.COMPLETED) : 0L;
        long booksPublished = bookRepository != null ? bookRepository.countByStatus(BookStatus.PUBLISHED) : 0L;
        long levelsPublished = levelRepository != null ? levelRepository.countByStatus(LearningStatus.PUBLISHED) : 0L;

        String csvContent = "Date,Metric,Value\n"
                + createdAt + ",TotalUsers," + totalUsers + "\n"
                + createdAt + ",ActiveLabs," + activeLabs + "\n"
                + createdAt + ",LearningAttempts," + attempts + "\n"
                + createdAt + ",LearningCompleted," + completed + "\n"
                + createdAt + ",BooksPublished," + booksPublished + "\n"
                + createdAt + ",LevelsPublished," + levelsPublished + "\n";

        byte[] fileBytes = csvContent.getBytes(StandardCharsets.UTF_8);

        if (assetStorageService != null) {
            try {
                assetStorageService.store(jobId + ".csv", fileBytes, "text/csv");
            } catch (Exception e) {
                inMemoryFiles.put(jobId, fileBytes);
            }
        } else {
            inMemoryFiles.put(jobId, fileBytes);
        }

        if (exportJobRepository != null) {
            AdminExportJobEntity jobEntity = new AdminExportJobEntity(
                    jobId,
                    "DASHBOARD_REPORT",
                    format,
                    "READY",
                    downloadUrl,
                    expiresAt
            );
            exportJobRepository.save(jobEntity);
        }

        Map<String, Object> job = new LinkedHashMap<>();
        job.put("jobId", jobId);
        job.put("status", "READY");
        job.put("format", format);
        job.put("downloadUrl", downloadUrl);
        job.put("createdAt", createdAt);
        job.put("expiresAt", expiresAt);
        inMemoryJobs.put(jobId, job);

        Map<String, Object> initialResponse = new LinkedHashMap<>();
        initialResponse.put("jobId", jobId);
        initialResponse.put("status", "QUEUED");
        initialResponse.put("createdAt", createdAt);

        return initialResponse;
    }

    @Override
    public Map<String, Object> getReportJob(String jobId) {
        if (exportJobRepository != null) {
            Optional<AdminExportJobEntity> entityOpt = exportJobRepository.findById(jobId);
            if (entityOpt.isPresent()) {
                AdminExportJobEntity entity = entityOpt.get();
                Map<String, Object> job = new LinkedHashMap<>();
                job.put("jobId", entity.getId());
                job.put("status", entity.getStatus());
                job.put("format", entity.getFormat());
                job.put("downloadUrl", entity.getDownloadUrl());
                job.put("createdAt", entity.getCreatedAt());
                job.put("expiresAt", entity.getExpiresAt());
                return job;
            }
        }

        Map<String, Object> job = inMemoryJobs.get(jobId);
        if (job == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND: Report job not found: " + jobId);
        }
        return job;
    }

    @Override
    public byte[] downloadReport(String jobId) {
        if (assetStorageService != null) {
            try {
                byte[] data = assetStorageService.load(jobId + ".csv");
                if (data != null) {
                    return data;
                }
            } catch (Exception ignored) {}
        }

        byte[] data = inMemoryFiles.get(jobId);
        if (data == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND: Report file not found: " + jobId);
        }
        return data;
    }
}
