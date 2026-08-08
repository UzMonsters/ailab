SELECT 'CREATE DATABASE ai_laboratory' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'ai_laboratory')\gexec

\c ai_laboratory

CREATE SCHEMA IF NOT EXISTS chemistry;
