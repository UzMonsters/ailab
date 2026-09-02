CREATE TABLE IF NOT EXISTS admin_audit_events (
    id VARCHAR(64) PRIMARY KEY,
    occurred_at TIMESTAMP WITH TIME ZONE NOT NULL,
    actor_id VARCHAR(64) NOT NULL,
    actor_name VARCHAR(100) NOT NULL,
    actor_role VARCHAR(50) NOT NULL,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(100) NOT NULL,
    entity_id VARCHAR(100) NOT NULL,
    entity_label VARCHAR(200),
    subject VARCHAR(100),
    source VARCHAR(50) NOT NULL,
    result VARCHAR(50) NOT NULL,
    severity VARCHAR(50) NOT NULL,
    before_state JSONB,
    after_state JSONB,
    changed_keys JSONB,
    request_id VARCHAR(100),
    ip_address VARCHAR(100),
    user_agent VARCHAR(500),
    metadata JSONB
);

CREATE INDEX IF NOT EXISTS idx_audit_occurred_at ON admin_audit_events(occurred_at);
CREATE INDEX IF NOT EXISTS idx_audit_actor_id ON admin_audit_events(actor_id);
CREATE INDEX IF NOT EXISTS idx_audit_action ON admin_audit_events(action);
CREATE INDEX IF NOT EXISTS idx_audit_entity_type ON admin_audit_events(entity_type);
CREATE INDEX IF NOT EXISTS idx_audit_severity ON admin_audit_events(severity);

CREATE TABLE IF NOT EXISTS admin_settings (
    id VARCHAR(64) PRIMARY KEY,
    settings_data JSONB NOT NULL,
    version BIGINT NOT NULL,
    etag VARCHAR(64) NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_by_id VARCHAR(64) NOT NULL,
    updated_by_name VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS admin_settings_history (
    id VARCHAR(64) PRIMARY KEY,
    version BIGINT NOT NULL,
    actor_id VARCHAR(64) NOT NULL,
    actor_name VARCHAR(100) NOT NULL,
    changed_keys JSONB NOT NULL,
    settings_snapshot JSONB NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_settings_history_version ON admin_settings_history(version);
CREATE INDEX IF NOT EXISTS idx_settings_history_created_at ON admin_settings_history(created_at);

CREATE TABLE IF NOT EXISTS admin_subjects (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    accent VARCHAR(50),
    sort_order INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS admin_catalog_drafts (
    id VARCHAR(64) PRIMARY KEY,
    entity_type VARCHAR(50) NOT NULL,
    code VARCHAR(100) NOT NULL,
    status VARCHAR(50) NOT NULL,
    data JSONB NOT NULL,
    version BIGINT NOT NULL DEFAULT 1,
    idempotency_key VARCHAR(100),
    published_version BIGINT,
    published_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_catalog_drafts_type_code ON admin_catalog_drafts(entity_type, code);
CREATE INDEX IF NOT EXISTS idx_catalog_drafts_status ON admin_catalog_drafts(status);

CREATE TABLE IF NOT EXISTS admin_export_jobs (
    id VARCHAR(64) PRIMARY KEY,
    job_type VARCHAR(50) NOT NULL,
    format VARCHAR(20) NOT NULL,
    status VARCHAR(50) NOT NULL,
    download_url VARCHAR(500),
    expires_at TIMESTAMP WITH TIME ZONE,
    error_message VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);
