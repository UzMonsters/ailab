-- V2: Create workspace sharing, membership, links, previews, chat, comments, and measurements

CREATE TABLE IF NOT EXISTS workspace_members (
    workspace_id VARCHAR(64) NOT NULL REFERENCES workspaces(id) ON DELETE CASCADE,
    user_id VARCHAR(64) NOT NULL,
    role VARCHAR(32) NOT NULL DEFAULT 'VIEWER',
    joined_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (workspace_id, user_id)
);

CREATE INDEX IF NOT EXISTS idx_workspace_members_user ON workspace_members(user_id);

INSERT INTO workspace_members (workspace_id, user_id, role, joined_at, created_at, updated_at)
SELECT w.id, w.owner_id, 'OWNER', w.created_at, w.created_at, w.updated_at
FROM workspaces w
WHERE NOT EXISTS (
    SELECT 1 FROM workspace_members wm WHERE wm.workspace_id = w.id AND wm.user_id = w.owner_id
);

CREATE TABLE IF NOT EXISTS workspace_invitations (
    id VARCHAR(64) PRIMARY KEY,
    workspace_id VARCHAR(64) NOT NULL REFERENCES workspaces(id) ON DELETE CASCADE,
    inviter_id VARCHAR(64) NOT NULL,
    email VARCHAR(255) NOT NULL,
    role VARCHAR(32) NOT NULL DEFAULT 'EDITOR',
    token_hash VARCHAR(128) NOT NULL UNIQUE,
    status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    expires_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_workspace_invitations_ws ON workspace_invitations(workspace_id);
CREATE INDEX IF NOT EXISTS idx_workspace_invitations_email ON workspace_invitations(email);

CREATE TABLE IF NOT EXISTS workspace_share_links (
    id VARCHAR(64) PRIMARY KEY,
    workspace_id VARCHAR(64) NOT NULL REFERENCES workspaces(id) ON DELETE CASCADE,
    token_hash VARCHAR(128) NOT NULL UNIQUE,
    role VARCHAR(32) NOT NULL DEFAULT 'VIEWER',
    password_hash VARCHAR(255),
    max_uses INTEGER,
    use_count INTEGER NOT NULL DEFAULT 0,
    allow_chat BOOLEAN NOT NULL DEFAULT TRUE,
    allow_comments BOOLEAN NOT NULL DEFAULT TRUE,
    expires_at TIMESTAMP WITH TIME ZONE,
    revoked_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_used_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_workspace_share_links_ws ON workspace_share_links(workspace_id);

CREATE TABLE IF NOT EXISTS workspace_previews (
    id VARCHAR(64) PRIMARY KEY,
    workspace_id VARCHAR(64) NOT NULL REFERENCES workspaces(id) ON DELETE CASCADE,
    source_state_version BIGINT NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'READY',
    dark_url TEXT,
    light_url TEXT,
    fallback_key VARCHAR(64),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_workspace_previews_ws ON workspace_previews(workspace_id);

CREATE TABLE IF NOT EXISTS workspace_chat_messages (
    id VARCHAR(64) PRIMARY KEY,
    client_message_id VARCHAR(128) NOT NULL,
    workspace_id VARCHAR(64) NOT NULL REFERENCES workspaces(id) ON DELETE CASCADE,
    author_id VARCHAR(64) NOT NULL,
    author_name VARCHAR(255),
    author_avatar TEXT,
    body TEXT NOT NULL,
    reply_to_id VARCHAR(64),
    anchor_item_id VARCHAR(64),
    anchor_version BIGINT,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    edited_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT uk_workspace_chat_client UNIQUE (workspace_id, client_message_id)
);

CREATE INDEX IF NOT EXISTS idx_workspace_chat_ws_created ON workspace_chat_messages(workspace_id, created_at DESC);

CREATE TABLE IF NOT EXISTS workspace_chat_reads (
    workspace_id VARCHAR(64) NOT NULL REFERENCES workspaces(id) ON DELETE CASCADE,
    user_id VARCHAR(64) NOT NULL,
    last_read_message_id VARCHAR(64),
    last_read_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (workspace_id, user_id)
);

CREATE TABLE IF NOT EXISTS workspace_comment_threads (
    id VARCHAR(64) PRIMARY KEY,
    workspace_id VARCHAR(64) NOT NULL REFERENCES workspaces(id) ON DELETE CASCADE,
    author_id VARCHAR(64) NOT NULL,
    author_name VARCHAR(255),
    author_avatar TEXT,
    anchor_item_id VARCHAR(64),
    anchor_point_x DOUBLE PRECISION,
    anchor_point_y DOUBLE PRECISION,
    anchor_version BIGINT,
    status VARCHAR(32) NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_workspace_comment_threads_ws ON workspace_comment_threads(workspace_id);

CREATE TABLE IF NOT EXISTS workspace_comment_replies (
    id VARCHAR(64) PRIMARY KEY,
    thread_id VARCHAR(64) NOT NULL REFERENCES workspace_comment_threads(id) ON DELETE CASCADE,
    author_id VARCHAR(64) NOT NULL,
    author_name VARCHAR(255),
    author_avatar TEXT,
    body TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_workspace_comment_replies_thread ON workspace_comment_replies(thread_id);

CREATE TABLE IF NOT EXISTS experiment_measurements (
    id VARCHAR(64) PRIMARY KEY,
    session_id VARCHAR(64) NOT NULL,
    workspace_id VARCHAR(64),
    sensor_item_id VARCHAR(64),
    target_item_id VARCHAR(64),
    kind VARCHAR(32) NOT NULL,
    "value" NUMERIC(20, 8) NOT NULL,
    unit VARCHAR(32) NOT NULL,
    recorded_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_experiment_measurements_session ON experiment_measurements(session_id, recorded_at);
CREATE INDEX IF NOT EXISTS idx_experiment_measurements_workspace ON experiment_measurements(workspace_id, recorded_at);
