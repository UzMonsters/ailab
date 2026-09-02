package com.ailab.admin.catalog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface AdminCatalogDraftRepository extends JpaRepository<AdminCatalogDraftEntity, String>, JpaSpecificationExecutor<AdminCatalogDraftEntity> {

    Optional<AdminCatalogDraftEntity> findByEntityTypeAndId(String entityType, String id);

    Optional<AdminCatalogDraftEntity> findByEntityTypeAndCode(String entityType, String code);

    List<AdminCatalogDraftEntity> findByEntityTypeAndStatus(String entityType, String status);
}
