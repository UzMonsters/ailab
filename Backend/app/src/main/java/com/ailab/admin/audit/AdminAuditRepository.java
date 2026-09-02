package com.ailab.admin.audit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AdminAuditRepository extends JpaRepository<AdminAuditEventEntity, String>, JpaSpecificationExecutor<AdminAuditEventEntity> {

    @Query("SELECT DISTINCT e.action FROM AdminAuditEventEntity e ORDER BY e.action")
    List<String> findDistinctActions();

    @Query("SELECT DISTINCT e.severity FROM AdminAuditEventEntity e ORDER BY e.severity")
    List<String> findDistinctSeverities();

    @Query("SELECT DISTINCT e.source FROM AdminAuditEventEntity e ORDER BY e.source")
    List<String> findDistinctSources();
}
