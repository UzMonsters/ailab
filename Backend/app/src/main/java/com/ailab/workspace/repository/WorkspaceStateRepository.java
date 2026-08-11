package com.ailab.workspace.repository;

import com.ailab.workspace.domain.WorkspaceStateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkspaceStateRepository extends JpaRepository<WorkspaceStateEntity, String> {
}
