-- Flyway Migration V23: Create Ionic Activity Reference Schema

CREATE TABLE IF NOT EXISTS chemistry.ionic_activity_parameter_sets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    model VARCHAR(32) NOT NULL,
    solvent_code VARCHAR(64) NOT NULL,
    temperature_celsius NUMERIC(6, 2) NOT NULL,
    davies_a NUMERIC(12, 8) NOT NULL CHECK (davies_a > 0),
    min_ionic_strength NUMERIC(12, 8) NOT NULL CHECK (min_ionic_strength >= 0),
    max_ionic_strength NUMERIC(12, 8) NOT NULL CHECK (max_ionic_strength >= min_ionic_strength),
    source_document TEXT NOT NULL,
    evidence TEXT NOT NULL,
    license TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_activity_parameter_context UNIQUE (model, solvent_code, temperature_celsius)
);

CREATE INDEX IF NOT EXISTS idx_activity_parameter_model_context
    ON chemistry.ionic_activity_parameter_sets(model, solvent_code, temperature_celsius);
