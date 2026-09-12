package com.ailab.admin.dashboard;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminExportJobRepository extends JpaRepository<AdminExportJobEntity, String> {

    Optional<AdminExportJobEntity> findByIdAndJobType(String id, String jobType);
}
