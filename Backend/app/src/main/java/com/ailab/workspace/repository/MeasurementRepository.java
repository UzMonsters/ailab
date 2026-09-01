package com.ailab.workspace.repository;

import com.ailab.workspace.domain.MeasurementEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeasurementRepository extends JpaRepository<MeasurementEntity, String> {

    List<MeasurementEntity> findByWorkspaceIdOrderByRecordedAtAsc(String workspaceId, Pageable pageable);

    List<MeasurementEntity> findByWorkspaceIdAndKindOrderByRecordedAtAsc(String workspaceId, String kind, Pageable pageable);

    List<MeasurementEntity> findBySessionIdOrderByRecordedAtAsc(String sessionId, Pageable pageable);

    List<MeasurementEntity> findBySessionIdAndKindOrderByRecordedAtAsc(String sessionId, String kind, Pageable pageable);
}
