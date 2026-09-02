package com.ailab.admin.dashboard;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    public AdminDashboardController(AdminDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard/summary")
    public Map<String, Object> getSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(required = false) String timezone,
            @RequestParam(required = false) String science) {
        return dashboardService.getSummary(from, to, timezone, science);
    }

    @GetMapping("/dashboard/activity-series")
    public Map<String, Object> getActivitySeries(
            @RequestParam(defaultValue = "experiments") String metric,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(defaultValue = "day") String bucket,
            @RequestParam(required = false) String timezone) {
        return dashboardService.getActivitySeries(metric, from, to, bucket, timezone);
    }

    @GetMapping("/dashboard/science-distribution")
    public Map<String, Object> getScienceDistribution(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(defaultValue = "labs") String metric) {
        return dashboardService.getScienceDistribution(from, to, metric);
    }

    @GetMapping("/dashboard/learning-summary")
    public Map<String, Object> getLearningSummary(
            @RequestParam(required = false) String track,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        return dashboardService.getLearningSummary(track, from, to);
    }

    @GetMapping("/dashboard/laboratory-summary")
    public Map<String, Object> getLaboratorySummary(
            @RequestParam(required = false) String science,
            @RequestParam(required = false) String status) {
        return dashboardService.getLaboratorySummary(science, status);
    }

    @GetMapping("/dashboard/activity-summary")
    public Map<String, Object> getActivitySummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant at,
            @RequestParam(required = false) String timezone) {
        return dashboardService.getActivitySummary(at, timezone);
    }

    @PostMapping("/reports")
    public ResponseEntity<Map<String, Object>> createReport(@RequestBody Map<String, Object> request) {
        Map<String, Object> job = dashboardService.createReport(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(job);
    }

    @GetMapping("/reports/{jobId}")
    public Map<String, Object> getReport(@PathVariable String jobId) {
        return dashboardService.getReportJob(jobId);
    }
}
