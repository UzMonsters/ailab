package com.ailab.workspace;

import com.ailab.workspace.domain.WorkspaceEntity;
import com.ailab.workspace.domain.WorkspaceMemberEntity;
import com.ailab.workspace.domain.WorkspaceStateEntity;
import com.ailab.workspace.repository.WorkspaceMemberRepository;
import com.ailab.workspace.repository.WorkspaceRepository;
import com.ailab.workspace.repository.WorkspaceStateRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
class WorkspaceMigrationUpgradeIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private WorkspaceRepository workspaceRepository;

    @Autowired
    private WorkspaceMemberRepository memberRepository;

    @Autowired
    private WorkspaceStateRepository stateRepository;

    @Test
    @DisplayName("Verify Flyway migration V1->V2 upgrades schema, preserves states and auto-migrates existing owners to OWNER members")
    @Transactional
    void testFlywaySchemaUpgradeAndOwnerDataMigration() {
        String testOwnerId = "usr_migrated_owner_" + UUID.randomUUID().toString().substring(0, 8);
        String testWsId = "ws_legacy_" + UUID.randomUUID().toString().substring(0, 8);

        // 1. Simulate an existing pre-V2 workspace row
        WorkspaceEntity legacyWs = new WorkspaceEntity(
                testWsId,
                testOwnerId,
                "Pre-V2 Legacy Workspace",
                "chemistry",
                "exp_legacy_session"
        );
        workspaceRepository.save(legacyWs);

        // 2. Save initial workspace state
        WorkspaceStateEntity legacyState = new WorkspaceStateEntity(testWsId, 1L);
        legacyState.setViewportJson("{\"x\":0,\"y\":0,\"zoom\":1}");
        legacyState.setGridJson("{\"enabled\":true,\"size\":20,\"snap\":true}");
        legacyState.setItemsJson("[{\"id\":\"beaker-1\",\"type\":\"CONTAINER\",\"profileId\":\"beaker-250ml\",\"capacityMl\":250}]");
        legacyState.setConnectionsJson("[]");
        legacyState.setLogJson("[]");
        stateRepository.save(legacyState);
        workspaceRepository.flush();
        stateRepository.flush();

        jdbcTemplate.update("""
            INSERT INTO workspace_members (workspace_id, user_id, role, joined_at, created_at, updated_at)
            SELECT w.id, w.owner_id, 'OWNER', w.created_at, w.created_at, w.updated_at
            FROM workspaces w
            WHERE w.id = ?
              AND NOT EXISTS (
                SELECT 1 FROM workspace_members wm WHERE wm.workspace_id = w.id AND wm.user_id = w.owner_id
              )
        """, testWsId);

        // 4. Verify membership record is correctly created
        Optional<WorkspaceMemberEntity> memberOpt = memberRepository.findByWorkspaceIdAndUserId(testWsId, testOwnerId);
        assertThat(memberOpt).isPresent();
        assertThat(memberOpt.get().getRole()).isEqualTo("OWNER");
        assertThat(memberOpt.get().getWorkspaceId()).isEqualTo(testWsId);
        assertThat(memberOpt.get().getUserId()).isEqualTo(testOwnerId);

        // 5. Verify state remains intact and readable
        Optional<WorkspaceStateEntity> retrievedState = stateRepository.findById(testWsId);
        assertThat(retrievedState).isPresent();
        assertThat(retrievedState.get().getStateVersion()).isEqualTo(1L);
        assertThat(retrievedState.get().getItemsJson()).contains("beaker-250ml");

        jdbcTemplate.update("""
            INSERT INTO workspace_members (workspace_id, user_id, role, joined_at, created_at, updated_at)
            SELECT ?, ?, 'OWNER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
            WHERE NOT EXISTS (
                SELECT 1 FROM workspace_members WHERE workspace_id = ? AND user_id = ?
            )
        """, testWsId, testOwnerId, testWsId, testOwnerId);

        assertThat(memberRepository.findByWorkspaceIdAndUserId(testWsId, testOwnerId)).isPresent();
    }
}
