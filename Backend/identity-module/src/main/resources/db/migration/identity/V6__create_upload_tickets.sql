CREATE TABLE IF NOT EXISTS upload_tickets (
    id VARCHAR(80) PRIMARY KEY,
    token_hash VARCHAR(64) NOT NULL UNIQUE,
    asset_id VARCHAR(120) NOT NULL,
    actor_id VARCHAR(120) NOT NULL,
    scope VARCHAR(40) NOT NULL,
    storage_key VARCHAR(700) NOT NULL,
    allowed_mime VARCHAR(120) NOT NULL,
    max_size_bytes BIGINT NOT NULL,
    expected_checksum VARCHAR(120),
    actual_checksum VARCHAR(120),
    actual_size_bytes BIGINT,
    actual_mime VARCHAR(120),
    status VARCHAR(24) NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    uploaded_at TIMESTAMP WITH TIME ZONE,
    completed_at TIMESTAMP WITH TIME ZONE,
    workspace_id VARCHAR(120),
    preview_id VARCHAR(120),
    variant VARCHAR(40),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT upload_tickets_status_chk CHECK (status IN ('ISSUED','UPLOADING','UPLOADED','COMPLETED','EXPIRED')),
    CONSTRAINT upload_tickets_size_chk CHECK (max_size_bytes > 0),
    CONSTRAINT upload_tickets_actual_size_chk CHECK (actual_size_bytes IS NULL OR actual_size_bytes > 0)
);

CREATE INDEX IF NOT EXISTS idx_upload_tickets_token_hash ON upload_tickets(token_hash);
CREATE INDEX IF NOT EXISTS idx_upload_tickets_actor_scope ON upload_tickets(actor_id, scope);
CREATE INDEX IF NOT EXISTS idx_upload_tickets_asset_scope ON upload_tickets(asset_id, scope);
CREATE INDEX IF NOT EXISTS idx_upload_tickets_status ON upload_tickets(status);
CREATE INDEX IF NOT EXISTS idx_upload_tickets_expires_at ON upload_tickets(expires_at);
CREATE INDEX IF NOT EXISTS idx_upload_tickets_workspace_preview ON upload_tickets(workspace_id, preview_id, asset_id);
