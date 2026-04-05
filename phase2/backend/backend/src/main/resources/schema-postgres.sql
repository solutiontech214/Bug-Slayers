-- ============================================
-- Centralized Log Aggregation Platform
-- PostgreSQL Schema with GIN Index for FTS
-- ============================================

-- Create database (run as superuser once)
-- CREATE DATABASE logplatform;

-- ============================================
-- GIN Index for Full-Text Search on logs
-- Run this AFTER Hibernate creates the table
-- ============================================

-- Add tsvector column if not exists
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name='logs' AND column_name='search_vector'
    ) THEN
        ALTER TABLE logs ADD COLUMN search_vector tsvector;
    END IF;
END $$;

-- Create GIN index for fast full-text search
CREATE INDEX IF NOT EXISTS idx_logs_search_gin
    ON logs USING GIN(search_vector);

-- Create trigger to auto-update search_vector on insert/update
CREATE OR REPLACE FUNCTION logs_search_vector_update()
RETURNS trigger AS $$
BEGIN
    NEW.search_vector := to_tsvector('english', coalesce(NEW.message, ''));
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS logs_search_vector_trigger ON logs;
CREATE TRIGGER logs_search_vector_trigger
    BEFORE INSERT OR UPDATE ON logs
    FOR EACH ROW EXECUTE FUNCTION logs_search_vector_update();

-- Extra performance indexes
CREATE INDEX IF NOT EXISTS idx_logs_level ON logs(level);
CREATE INDEX IF NOT EXISTS idx_logs_sub_module_id ON logs(sub_module_id);
CREATE INDEX IF NOT EXISTS idx_logs_project_id ON logs(project_id);
CREATE INDEX IF NOT EXISTS idx_logs_timestamp ON logs(timestamp DESC);
CREATE INDEX IF NOT EXISTS idx_logs_created_at ON logs(created_at DESC);

-- Issues indexes
CREATE INDEX IF NOT EXISTS idx_issues_status ON issues(status);
CREATE INDEX IF NOT EXISTS idx_issues_severity ON issues(severity);
CREATE INDEX IF NOT EXISTS idx_issues_project_id ON issues(project_id);
