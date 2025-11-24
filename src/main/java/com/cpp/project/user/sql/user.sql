-- ============================================================================
-- USER MANAGEMENT SYSTEM - DATABASE SCHEMA
-- Database: PostgreSQL
-- ============================================================================

-- Drop tables if they exist (for clean setup)
DROP TABLE IF EXISTS users CASCADE;

-- Drop existing functions and triggers
DROP TRIGGER IF EXISTS update_users_updated_at ON users;
DROP FUNCTION IF EXISTS update_updated_at_column() CASCADE;

-- ============================================================================
-- USERS TABLE
-- ============================================================================

CREATE TABLE IF NOT EXISTS users
(
    id         UUID PRIMARY KEY,
    name       VARCHAR(255)             NOT NULL,
    email      VARCHAR(255)             NOT NULL UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- INDEXES FOR PERFORMANCE
-- ============================================================================

-- Index on email for fast lookups during login
CREATE INDEX idx_users_email ON users (email);

-- Index on created_at for sorting/filtering users by registration date
CREATE INDEX idx_users_created_at ON users (created_at DESC);

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

-- Trigger for users table
CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE
    ON users
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

-- ============================================================================
-- COMMENTS (Documentation)
-- ============================================================================

COMMENT ON TABLE users IS 'Stores user account information';
COMMENT ON COLUMN users.id IS 'Unique identifier for the user (UUID v4)';
COMMENT ON COLUMN users.name IS 'Full name of the user';
COMMENT ON COLUMN users.email IS 'Email address (unique, used for login)';
COMMENT ON COLUMN users.created_at IS 'Timestamp when the user account was created';
COMMENT ON COLUMN users.updated_at IS 'Timestamp when the user record was last updated';

-- ============================================================================
-- SAMPLE DATA (For Testing)
-- ============================================================================

-- Insert sample users
INSERT INTO users (id, name, email)
VALUES ('550e8400-e29b-41d4-a716-446655440000'::uuid, 'John Doe', 'john.doe@example.com'),
       ('550e8400-e29b-41d4-a716-446655440001'::uuid, 'Jane Smith', 'jane.smith@example.com'),
       ('550e8400-e29b-41d4-a716-446655440002'::uuid, 'Bob Wilson', 'bob.wilson@example.com');

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

-- 1. Find user by email (used in login)
-- SELECT * FROM users WHERE email = 'john.doe@example.com';

-- 2. Count total users
-- SELECT COUNT(*) FROM users;

-- 3. Find recently registered users (last 7 days)
-- SELECT id, name, email, created_at
-- FROM users
-- WHERE created_at > CURRENT_TIMESTAMP - INTERVAL '7 days'
-- ORDER BY created_at DESC;

-- 4. Update user information
-- UPDATE users
-- SET name = 'John Smith', email = 'john.smith@example.com'
-- WHERE id = '550e8400-e29b-41d4-a716-446655440000'::uuid;

-- 5. Delete user (cascade will delete credentials)
-- DELETE FROM users WHERE id = '550e8400-e29b-41d4-a716-446655440000'::uuid;

-- 6. Check if email exists
-- SELECT EXISTS(SELECT 1 FROM users WHERE email = 'test@example.com') AS email_exists;

-- 7. Get user count by creation month
-- SELECT
--     DATE_TRUNC('month', created_at) AS month,
--     COUNT(*) AS user_count
-- FROM users
-- GROUP BY DATE_TRUNC('month', created_at)
-- ORDER BY month DESC;

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