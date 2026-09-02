CREATE TABLE IF NOT EXISTS learning_tracks (
    id VARCHAR(64) PRIMARY KEY,
    code VARCHAR(64) NOT NULL UNIQUE,
    sort_order INT NOT NULL DEFAULT 1,
    default_locale VARCHAR(10) NOT NULL DEFAULT 'ru',
    translations JSONB NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    draft_version BIGINT NOT NULL DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_learning_tracks_code ON learning_tracks(code);
CREATE INDEX IF NOT EXISTS idx_learning_tracks_status ON learning_tracks(status);

CREATE TABLE IF NOT EXISTS learning_levels (
    id VARCHAR(64) PRIMARY KEY,
    track_id VARCHAR(64) NOT NULL REFERENCES learning_tracks(id) ON DELETE CASCADE,
    level_number INT NOT NULL,
    sort_order INT NOT NULL DEFAULT 1,
    difficulty VARCHAR(30) NOT NULL DEFAULT 'BEGINNER',
    estimated_minutes INT NOT NULL DEFAULT 10,
    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    draft_version BIGINT NOT NULL DEFAULT 1,
    published_version BIGINT,
    prerequisites JSONB NOT NULL DEFAULT '[]'::jsonb,
    requirements JSONB NOT NULL DEFAULT '{}'::jsonb,
    available_equipment JSONB NOT NULL DEFAULT '[]'::jsonb,
    available_materials JSONB NOT NULL DEFAULT '[]'::jsonb,
    scenario JSONB NOT NULL DEFAULT '{}'::jsonb,
    steps JSONB NOT NULL DEFAULT '[]'::jsonb,
    rewards JSONB NOT NULL DEFAULT '{}'::jsonb,
    translations JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_learning_levels_track_number UNIQUE (track_id, level_number)
);

CREATE INDEX IF NOT EXISTS idx_learning_levels_track ON learning_levels(track_id);
CREATE INDEX IF NOT EXISTS idx_learning_levels_status ON learning_levels(status);

CREATE TABLE IF NOT EXISTS learning_level_published_snapshots (
    id VARCHAR(64) PRIMARY KEY,
    level_id VARCHAR(64) NOT NULL REFERENCES learning_levels(id) ON DELETE CASCADE,
    version BIGINT NOT NULL,
    release_note VARCHAR(500),
    published_by_id VARCHAR(64) NOT NULL,
    published_by_name VARCHAR(100) NOT NULL,
    snapshot_data JSONB NOT NULL,
    idempotency_key VARCHAR(128),
    published_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_learning_level_snapshots UNIQUE (level_id, version)
);

CREATE INDEX IF NOT EXISTS idx_learning_level_snapshots_idempotency ON learning_level_published_snapshots(idempotency_key);

CREATE TABLE IF NOT EXISTS learning_chapters (
    id VARCHAR(64) PRIMARY KEY,
    track_id VARCHAR(64) NOT NULL REFERENCES learning_tracks(id) ON DELETE CASCADE,
    sort_order INT NOT NULL DEFAULT 1,
    level_ids JSONB NOT NULL DEFAULT '[]'::jsonb,
    translations JSONB NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_learning_chapters_track ON learning_chapters(track_id, sort_order);

CREATE TABLE IF NOT EXISTS learning_tasks (
    id VARCHAR(64) PRIMARY KEY,
    code VARCHAR(64) NOT NULL UNIQUE,
    task_type VARCHAR(50) NOT NULL,
    validation_rule JSONB NOT NULL DEFAULT '{}'::jsonb,
    guide_template JSONB NOT NULL DEFAULT '{}'::jsonb,
    translations JSONB NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_learning_tasks_code ON learning_tasks(code);

CREATE TABLE IF NOT EXISTS learning_rewards (
    id VARCHAR(64) PRIMARY KEY,
    code VARCHAR(64) NOT NULL UNIQUE,
    reward_type VARCHAR(30) NOT NULL DEFAULT 'BADGE',
    asset_id VARCHAR(64),
    criteria JSONB NOT NULL DEFAULT '{}'::jsonb,
    translations JSONB NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_learning_rewards_code ON learning_rewards(code);

CREATE TABLE IF NOT EXISTS learning_user_progress (
    id VARCHAR(128) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    track_id VARCHAR(64) NOT NULL REFERENCES learning_tracks(id) ON DELETE CASCADE,
    completed_level_ids JSONB NOT NULL DEFAULT '[]'::jsonb,
    current_level_id VARCHAR(64),
    badges JSONB NOT NULL DEFAULT '[]'::jsonb,
    unlocked_equipment JSONB NOT NULL DEFAULT '[]'::jsonb,
    unlocked_materials JSONB NOT NULL DEFAULT '[]'::jsonb,
    unlocked_book_chapters JSONB NOT NULL DEFAULT '[]'::jsonb,
    stats JSONB NOT NULL DEFAULT '{}'::jsonb,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_learning_user_progress UNIQUE (user_id, track_id)
);

CREATE INDEX IF NOT EXISTS idx_learning_progress_user ON learning_user_progress(user_id);

CREATE TABLE IF NOT EXISTS learning_user_attempts (
    id VARCHAR(64) PRIMARY KEY,
    client_attempt_id VARCHAR(128),
    user_id VARCHAR(64),
    is_guest BOOLEAN NOT NULL DEFAULT FALSE,
    is_preview BOOLEAN NOT NULL DEFAULT FALSE,
    level_id VARCHAR(64) NOT NULL REFERENCES learning_levels(id) ON DELETE CASCADE,
    level_version BIGINT NOT NULL,
    workspace_id VARCHAR(64) NOT NULL,
    experiment_id VARCHAR(64) NOT NULL,
    state_version BIGINT NOT NULL DEFAULT 1,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    current_step_index INT NOT NULL DEFAULT 0,
    current_step_id VARCHAR(64) NOT NULL,
    completed_steps JSONB NOT NULL DEFAULT '[]'::jsonb,
    hint_usage JSONB NOT NULL DEFAULT '[]'::jsonb,
    idempotency_key VARCHAR(128),
    started_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_learning_attempts_user_level ON learning_user_attempts(user_id, level_id);
CREATE INDEX IF NOT EXISTS idx_learning_attempts_client_id ON learning_user_attempts(client_attempt_id);
CREATE INDEX IF NOT EXISTS idx_learning_attempts_workspace ON learning_user_attempts(workspace_id);

CREATE TABLE IF NOT EXISTS learning_progress_reset_audit (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    track_id VARCHAR(64),
    level_id VARCHAR(64),
    reason VARCHAR(500) NOT NULL,
    initiated_by VARCHAR(64) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'QUEUED',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
