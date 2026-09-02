package com.ailab.admin.settings;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminSubjectRepository extends JpaRepository<AdminSubjectEntity, String> {
    List<AdminSubjectEntity> findAllByOrderBySortOrderAsc();
}
