package com.ailab.workspace.service;

import com.ailab.workspace.domain.MeasurementEntity;
import com.ailab.workspace.dto.MeasurementPointDto;
import com.ailab.workspace.repository.MeasurementRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class MeasurementService {

    private final MeasurementRepository measurementRepository;

    public MeasurementService(MeasurementRepository measurementRepository) {
        this.measurementRepository = measurementRepository;
    }

    public List<MeasurementPointDto> getMeasurements(String sessionId, String workspaceId, String kind, Instant from, Instant to, int limit) {
        int safeLimit = Math.max(1, Math.min(limit <= 0 ? 100 : limit, 1000));
        PageRequest page = PageRequest.of(0, safeLimit);

        List<MeasurementEntity> entities;
        String normalizedKind = kind != null && !kind.isBlank() ? kind.toUpperCase() : null;

        if (workspaceId != null && !workspaceId.isBlank()) {
            if (normalizedKind != null) {
                entities = measurementRepository.findByWorkspaceIdAndKindOrderByRecordedAtAsc(workspaceId, normalizedKind, page);
            } else {
                entities = measurementRepository.findByWorkspaceIdOrderByRecordedAtAsc(workspaceId, page);
            }
        } else {
            if (normalizedKind != null) {
                entities = measurementRepository.findBySessionIdAndKindOrderByRecordedAtAsc(sessionId, normalizedKind, page);
            } else {
                entities = measurementRepository.findBySessionIdOrderByRecordedAtAsc(sessionId, page);
            }
        }

        if (from != null) {
            entities = entities.stream().filter(e -> !e.getRecordedAt().isBefore(from)).toList();
        }
        if (to != null) {
            entities = entities.stream().filter(e -> !e.getRecordedAt().isAfter(to)).toList();
        }

        return entities.stream().map(m -> new MeasurementPointDto(
                m.getId(),
                m.getKind(),
                m.getValue(),
                m.getUnit(),
                m.getSensorItemId(),
                m.getTargetItemId(),
                m.getRecordedAt()
        )).toList();
    }

    @Transactional
    public MeasurementPointDto recordMeasurement(String sessionId, String workspaceId, String sensorId, String targetId, String kind, BigDecimal value, String unit) {
        MeasurementEntity m = new MeasurementEntity(
                UUID.randomUUID().toString(),
                sessionId,
                workspaceId,
                sensorId,
                targetId,
                kind != null ? kind.toUpperCase() : "TEMPERATURE",
                value,
                unit != null ? unit : "°C",
                Instant.now()
        );
        measurementRepository.save(m);
        return new MeasurementPointDto(m.getId(), m.getKind(), m.getValue(), m.getUnit(), m.getSensorItemId(), m.getTargetItemId(), m.getRecordedAt());
    }
}
