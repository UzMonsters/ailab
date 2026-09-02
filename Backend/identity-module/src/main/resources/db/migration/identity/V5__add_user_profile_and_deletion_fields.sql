ALTER TABLE users ADD COLUMN IF NOT EXISTS display_name VARCHAR(100);
ALTER TABLE users ADD COLUMN IF NOT EXISTS bio VARCHAR(500);
ALTER TABLE users ADD COLUMN IF NOT EXISTS deletion_id VARCHAR(64);
ALTER TABLE users ADD COLUMN IF NOT EXISTS deletion_scheduled_for TIMESTAMP WITH TIME ZONE;

CREATE INDEX IF NOT EXISTS idx_users_deletion_id ON users(deletion_id);
CREATE INDEX IF NOT EXISTS idx_users_deletion_scheduled_for ON users(deletion_scheduled_for);
