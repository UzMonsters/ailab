CREATE TABLE chemistry_engine_metadata (
    id SERIAL PRIMARY KEY,
    engine_version VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO chemistry_engine_metadata (engine_version) VALUES ('1.0.0');
