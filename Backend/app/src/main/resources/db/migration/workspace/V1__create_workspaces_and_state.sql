CREATE TABLE IF NOT EXISTS workspaces (
    id VARCHAR(64) PRIMARY KEY,
    owner_id VARCHAR(64) NOT NULL,
    name VARCHAR(255) NOT NULL,
    science VARCHAR(32) NOT NULL DEFAULT 'chemistry',
    thumbnail TEXT,
    state_version BIGINT NOT NULL DEFAULT 1,
    is_favorite BOOLEAN NOT NULL DEFAULT FALSE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    experiment_session_id VARCHAR(64),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_workspaces_owner ON workspaces(owner_id);
CREATE INDEX IF NOT EXISTS idx_workspaces_science ON workspaces(science);
CREATE INDEX IF NOT EXISTS idx_workspaces_deleted ON workspaces(is_deleted);

CREATE TABLE IF NOT EXISTS workspace_states (
    workspace_id VARCHAR(64) PRIMARY KEY REFERENCES workspaces(id) ON DELETE CASCADE,
    state_version BIGINT NOT NULL DEFAULT 1,
    viewport_json TEXT,
    grid_json TEXT,
    items_json TEXT,
    connections_json TEXT,
    log_json TEXT,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS workspace_events (
    id VARCHAR(64) PRIMARY KEY,
    workspace_id VARCHAR(64) NOT NULL REFERENCES workspaces(id) ON DELETE CASCADE,
    user_id VARCHAR(64) NOT NULL,
    client_event_id VARCHAR(128) NOT NULL,
    event_type VARCHAR(64) NOT NULL,
    version BIGINT NOT NULL,
    payload_json TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_workspace_client_event UNIQUE (workspace_id, user_id, client_event_id)
);

CREATE INDEX IF NOT EXISTS idx_workspace_events_ws_ver ON workspace_events(workspace_id, version);
