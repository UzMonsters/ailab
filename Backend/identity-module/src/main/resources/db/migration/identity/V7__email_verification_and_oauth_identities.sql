-- Normalize existing emails and usernames to canonical format
UPDATE users SET email = LOWER(TRIM(email)), username = TRIM(username);

-- Allow nullable password_hash for OAuth/Google-only accounts
ALTER TABLE users ALTER COLUMN password_hash DROP NOT NULL;

-- Email verification timestamp
ALTER TABLE users ADD COLUMN IF NOT EXISTS email_verified_at TIMESTAMP WITH TIME ZONE;

-- Safe legacy user policy: grandfather existing users with created_at timestamp
UPDATE users SET email_verified_at = created_at WHERE email_verified_at IS NULL;

-- Email and username uniqueness is enforced via canonicalization and existing unique constraints on email and username

-- Provider identities table (e.g. Google OIDC identities)
CREATE TABLE IF NOT EXISTS user_auth_identities (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    provider VARCHAR(32) NOT NULL,
    provider_subject VARCHAR(255) NOT NULL,
    provider_email VARCHAR(320),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uq_user_auth_identities_provider_subject UNIQUE (provider, provider_subject)
);

CREATE INDEX IF NOT EXISTS idx_user_auth_identities_user_id ON user_auth_identities(user_id);

-- Email verification challenges / tokens table
CREATE TABLE IF NOT EXISTS email_verification_tokens (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash VARCHAR(64) NOT NULL UNIQUE,
    purpose VARCHAR(32) NOT NULL,
    new_email VARCHAR(320),
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    consumed_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_email_verification_tokens_user_purpose ON email_verification_tokens(user_id, purpose);
CREATE INDEX IF NOT EXISTS idx_email_verification_tokens_expires_at ON email_verification_tokens(expires_at);