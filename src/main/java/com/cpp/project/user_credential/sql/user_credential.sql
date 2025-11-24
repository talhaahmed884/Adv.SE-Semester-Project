-- ============================================================================
-- USER MANAGEMENT SYSTEM - DATABASE SCHEMA
-- Database: PostgreSQL
-- ============================================================================

-- Drop tables if they exist (for clean setup)
DROP TABLE IF EXISTS user_credentials CASCADE;

-- Drop existing functions and triggers
DROP TRIGGER IF EXISTS update_user_credentials_updated_at ON user_credentials;
DROP FUNCTION IF EXISTS update_updated_at_column() CASCADE;

-- ============================================================================
-- USER_CREDENTIALS TABLE
-- ============================================================================

CREATE TABLE IF NOT EXISTS user_credentials
(
    user_id       UUID PRIMARY KEY,
    password_hash VARCHAR(512)             NOT NULL,
    algorithm     VARCHAR(50)              NOT NULL DEFAULT 'SHA-512',
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign Key Constraint
    CONSTRAINT fk_user_credentials_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE
            ON UPDATE CASCADE
);

-- ============================================================================
-- TRIGGERS FOR UPDATED_AT TIMESTAMP
-- ============================================================================

-- Function to automatically update the updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger for user_credentials table
CREATE TRIGGER update_user_credentials_updated_at
    BEFORE UPDATE
    ON user_credentials
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

-- ============================================================================
-- COMMENTS (Documentation)
-- ============================================================================

COMMENT ON TABLE user_credentials IS 'Stores user authentication credentials';
COMMENT ON COLUMN user_credentials.user_id IS 'Foreign key reference to users table (one-to-one relationship)';
COMMENT ON COLUMN user_credentials.password_hash IS 'Hashed password with salt (Base64 encoded)';
COMMENT ON COLUMN user_credentials.algorithm IS 'Hashing algorithm used (default: SHA-512)';
COMMENT ON COLUMN user_credentials.created_at IS 'Timestamp when the credentials were created';
COMMENT ON COLUMN user_credentials.updated_at IS 'Timestamp when the credentials were last updated';

-- ============================================================================
-- SAMPLE DATA (For Testing)
-- ============================================================================

-- Insert sample credentials (Note: These are example hashes, not real passwords)
INSERT INTO user_credentials (user_id, password_hash, algorithm)
VALUES ('550e8400-e29b-41d4-a716-446655440000'::uuid,
        'aGFzaGVkX3Bhc3N3b3JkXzEyMzQ1Njc4OTBhYmNkZWZnaGlqa2xtbm9wcXJzdHV2d3h5eg==',
        'SHA-512'),
       ('550e8400-e29b-41d4-a716-446655440001'::uuid,
        'YW5vdGhlcl9oYXNoZWRfcGFzc3dvcmRfZm9yX3Rlc3Rpbmdfb25seQ==',
        'SHA-512'),
       ('550e8400-e29b-41d4-a716-446655440002'::uuid,
        'dGhpcmRfdXNlcl9oYXNoZWRfcGFzc3dvcmRfZXhhbXBsZQ==',
        'SHA-512');

-- ============================================================================
-- COMMON QUERIES
-- ============================================================================

-- 1. Authenticate user - get user with credentials
-- SELECT u.id, u.name, u.email, uc.password_hash, uc.algorithm
-- FROM users u
-- INNER JOIN user_credentials uc ON u.id = uc.user_id
-- WHERE u.email = 'john.doe@example.com';

-- 2. Get all users with their credential info
-- SELECT u.id, u.name, u.email, u.created_at, uc.algorithm, uc.created_at AS credential_created
-- FROM users u
-- LEFT JOIN user_credentials uc ON u.id = uc.user_id
-- ORDER BY u.created_at DESC;

-- 3. Update password (hash should be generated in application)
-- UPDATE user_credentials
-- SET password_hash = 'new_hashed_password_here'
-- WHERE user_id = '550e8400-e29b-41d4-a716-446655440000'::uuid;

-- 4. Delete user (cascade will delete credentials)
-- DELETE FROM users WHERE id = '550e8400-e29b-41d4-a716-446655440000'::uuid;

-- ============================================================================
-- PERFORMANCE ANALYSIS QUERIES
-- ============================================================================

-- Check index usage statistics
-- SELECT
--     schemaname,
--     tablename,
--     indexname,
--     idx_scan AS index_scans,
--     idx_tup_read AS tuples_read,
--     idx_tup_fetch AS tuples_fetched
-- FROM pg_stat_user_indexes
-- WHERE schemaname = 'public'
-- ORDER BY idx_scan DESC;

-- Check table statistics
-- SELECT
--     schemaname,
--     tablename,
--     n_live_tup AS live_rows,
--     n_dead_tup AS dead_rows,
--     last_vacuum,
--     last_autovacuum
-- FROM pg_stat_user_tables
-- WHERE schemaname = 'public';

-- Analyze query performance
-- EXPLAIN ANALYZE
-- SELECT u.*
-- FROM users u
-- INNER JOIN user_credentials uc ON u.id = uc.user_id
-- WHERE u.email = 'john.doe@example.com';

-- ============================================================================
-- MAINTENANCE TASKS
-- ============================================================================

-- Vacuum tables to reclaim space and update statistics
-- VACUUM ANALYZE users;
-- VACUUM ANALYZE user_credentials;

-- Reindex tables (if needed after bulk operations)
-- REINDEX TABLE users;
-- REINDEX TABLE user_credentials;

-- Update table statistics
-- ANALYZE users;
-- ANALYZE user_credentials;

-- ============================================================================
-- BACKUP AND RESTORE
-- ============================================================================

-- Backup specific tables:
-- pg_dump -U username -d database_name -t users -t user_credentials > user_backup.sql

-- Restore:
-- psql -U username -d database_name < user_backup.sql

-- ============================================================================
-- DATABASE USER SETUP (Security)
-- ============================================================================

-- Create application database user with limited permissions
-- CREATE USER app_user WITH PASSWORD 'secure_password_here';
-- GRANT CONNECT ON DATABASE your_database TO app_user;
-- GRANT USAGE ON SCHEMA public TO app_user;
-- GRANT SELECT, INSERT, UPDATE, DELETE ON users, user_credentials TO app_user;
-- GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO app_user;