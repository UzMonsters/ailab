CREATE TABLE IF NOT EXISTS books (
    id VARCHAR(64) PRIMARY KEY,
    slug VARCHAR(120) NOT NULL UNIQUE,
    default_locale VARCHAR(10) NOT NULL DEFAULT 'ru',
    translations JSONB NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    draft_version BIGINT NOT NULL DEFAULT 1,
    published_version BIGINT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_books_slug ON books(slug);
CREATE INDEX IF NOT EXISTS idx_books_status ON books(status);

CREATE TABLE IF NOT EXISTS book_chapters (
    id VARCHAR(64) PRIMARY KEY,
    book_id VARCHAR(64) NOT NULL REFERENCES books(id) ON DELETE CASCADE,
    position INT NOT NULL DEFAULT 1,
    translations JSONB NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_book_chapters_book_pos ON book_chapters(book_id, position);

CREATE TABLE IF NOT EXISTS book_pages (
    id VARCHAR(64) PRIMARY KEY,
    chapter_id VARCHAR(64) NOT NULL REFERENCES book_chapters(id) ON DELETE CASCADE,
    book_id VARCHAR(64) NOT NULL REFERENCES books(id) ON DELETE CASCADE,
    slug VARCHAR(120) NOT NULL,
    position INT NOT NULL DEFAULT 1,
    layout VARCHAR(50) NOT NULL DEFAULT 'single-page',
    translations JSONB NOT NULL,
    blocks JSONB NOT NULL DEFAULT '[]'::jsonb,
    version BIGINT NOT NULL DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_book_pages_chapter_pos ON book_pages(chapter_id, position);
CREATE INDEX IF NOT EXISTS idx_book_pages_book_slug ON book_pages(book_id, slug);

CREATE TABLE IF NOT EXISTS book_assets (
    id VARCHAR(64) PRIMARY KEY,
    kind VARCHAR(30) NOT NULL DEFAULT 'IMAGE',
    mime_type VARCHAR(100) NOT NULL,
    size_bytes BIGINT NOT NULL DEFAULT 0,
    checksum VARCHAR(128),
    status VARCHAR(30) NOT NULL DEFAULT 'READY',
    variants JSONB,
    width INT,
    height INT,
    alt JSONB,
    caption JSONB,
    upload_url VARCHAR(500),
    download_url VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_book_assets_status ON book_assets(status);

CREATE TABLE IF NOT EXISTS book_published_snapshots (
    id VARCHAR(64) PRIMARY KEY,
    book_id VARCHAR(64) NOT NULL REFERENCES books(id) ON DELETE CASCADE,
    version BIGINT NOT NULL,
    release_note VARCHAR(500),
    published_by_id VARCHAR(64) NOT NULL,
    published_by_name VARCHAR(100) NOT NULL,
    snapshot_data JSONB NOT NULL,
    idempotency_key VARCHAR(128),
    published_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_book_published_snapshots UNIQUE (book_id, version)
);

CREATE INDEX IF NOT EXISTS idx_book_snapshots_idempotency ON book_published_snapshots(idempotency_key);

CREATE TABLE IF NOT EXISTS book_user_progress (
    id VARCHAR(128) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    book_id VARCHAR(64) NOT NULL,
    page_id VARCHAR(64),
    scroll_anchor VARCHAR(100),
    bookmarks JSONB NOT NULL DEFAULT '[]'::jsonb,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_book_user_progress UNIQUE (user_id, book_id)
);

CREATE INDEX IF NOT EXISTS idx_book_progress_user ON book_user_progress(user_id);
