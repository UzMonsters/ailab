package com.ailab.admin.settings;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface AdminSettingsHistoryRepository extends JpaRepository<AdminSettingsHistoryEntity, String>, JpaSpecificationExecutor<AdminSettingsHistoryEntity> {
    Optional<AdminSettingsHistoryEntity> findByVersion(Long version);
}
