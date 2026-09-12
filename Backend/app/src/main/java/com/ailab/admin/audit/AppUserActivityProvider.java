package com.ailab.admin.audit;

import com.ailab.user.api.UserDtos;
import com.ailab.user.domain.User;
import com.ailab.user.service.UserActivityProvider;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class AppUserActivityProvider implements UserActivityProvider {

    private final AdminAuditRepository auditRepository;

    public AppUserActivityProvider(AdminAuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    @Override
    public List<UserDtos.UserActivity> getUserActivities(User user, Instant from, Instant to, String type) {
        if (user == null) {
            return List.of();
        }

        List<AdminAuditEventEntity> events = auditRepository.findTop20ByActorIdOrderByOccurredAtDesc(user.getId());
        List<UserDtos.UserActivity> activities = new ArrayList<>();

        for (AdminAuditEventEntity e : events) {
            if (from != null && e.getOccurredAt().isBefore(from)) {
                continue;
            }
            if (to != null && !e.getOccurredAt().isBefore(to)) {
                continue;
            }
            if (type != null && !type.isBlank()) {
                boolean matchesType = (e.getEntityType() != null && e.getEntityType().equalsIgnoreCase(type))
                        || (e.getAction() != null && e.getAction().toLowerCase().contains(type.toLowerCase()));
                if (!matchesType) {
                    continue;
                }
            }

            activities.add(new UserDtos.UserActivity(
                    e.getId(),
                    e.getOccurredAt(),
                    e.getEntityType(),
                    e.getAction(),
                    e.getEntityLabel() != null ? e.getEntityLabel() : e.getAction(),
                    e.getIpAddress(),
                    e.getUserAgent()
            ));
        }

        return activities;
    }
}
