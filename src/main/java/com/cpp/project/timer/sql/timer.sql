-- ============================================================================
-- TASK TIMER SYSTEM - DATABASE SCHEMA
-- Database: PostgreSQL
-- ============================================================================

-- Drop tables if they exist (for clean setup)
DROP TABLE IF EXISTS task_timers CASCADE;

-- Drop existing triggers
DROP TRIGGER IF EXISTS update_task_timers_updated_at ON task_timers CASCADE;

-- ============================================================================
-- TASK_TIMERS TABLE
-- Stores timer sessions for course tasks with accumulation support
-- ============================================================================

CREATE TABLE IF NOT EXISTS task_timers
(
    id              UUID PRIMARY KEY,
    user_id         UUID                     NOT NULL,
    course_task_id  UUID                     NOT NULL,
    start_time      TIMESTAMP WITH TIME ZONE NOT NULL,
    end_time        TIMESTAMP WITH TIME ZONE,
    duration_millis BIGINT                   NOT NULL DEFAULT 0,
    status          VARCHAR(50)              NOT NULL DEFAULT 'STOPPED',
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign key to users table
    CONSTRAINT fk_timer_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,

    -- Foreign key to course_tasks table
    CONSTRAINT fk_timer_task FOREIGN KEY (course_task_id) REFERENCES course_tasks (id) ON DELETE CASCADE,

    -- Constraint on status values (RUNNING, STOPPED)
    CONSTRAINT chk_timer_status CHECK (status IN ('RUNNING', 'STOPPED')),

    -- Constraint: end_time must be after start_time
    CONSTRAINT chk_timer_end_after_start CHECK (end_time IS NULL OR end_time > start_time),

    -- Constraint: duration must be non-negative
    CONSTRAINT chk_timer_duration CHECK (duration_millis >= 0)
);

-- ============================================================================
-- INDEXES FOR PERFORMANCE
-- ============================================================================

-- Index on user_id for fast lookups of user's timers
CREATE INDEX idx_task_timers_user_id ON task_timers (user_id);

-- Index on course_task_id for fast lookups of task's timers
CREATE INDEX idx_task_timers_course_task_id ON task_timers (course_task_id);

-- Index on status for filtering active timers
CREATE INDEX idx_task_timers_status ON task_timers (status);

-- Composite index for finding active timer by user and task (critical for concurrent timer prevention)
CREATE INDEX idx_task_timers_user_task_status ON task_timers (user_id, course_task_id, status);

-- ============================================================================
-- TRIGGERS FOR UPDATED_AT TIMESTAMP
-- ============================================================================

-- Reuse existing update_updated_at_column() function from course.sql
-- No need to recreate the function, just reference it in the trigger

-- Trigger for task_timers table
CREATE TRIGGER update_task_timers_updated_at
    BEFORE UPDATE
    ON task_timers
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

-- ============================================================================
-- COMMENTS (Documentation)
-- ============================================================================

COMMENT ON TABLE task_timers IS 'Stores timer sessions for course tasks with accumulation support';
COMMENT ON COLUMN task_timers.id IS 'Unique identifier for the timer session (UUID)';
COMMENT ON COLUMN task_timers.user_id IS 'User who owns this timer session';
COMMENT ON COLUMN task_timers.course_task_id IS 'Course task being timed';
COMMENT ON COLUMN task_timers.start_time IS 'When timer session started (UTC)';
COMMENT ON COLUMN task_timers.end_time IS 'When timer session ended (UTC) - NULL if running';
COMMENT ON COLUMN task_timers.duration_millis IS 'Duration in milliseconds (calculated on stop)';
COMMENT ON COLUMN task_timers.status IS 'Timer status (RUNNING, STOPPED)';
COMMENT ON COLUMN task_timers.created_at IS 'Timestamp when the timer was created';
COMMENT ON COLUMN task_timers.updated_at IS 'Timestamp when the timer was last updated';

-- ============================================================================
-- SAMPLE DATA (For Testing)
-- ============================================================================

-- Note: Replace UUIDs with actual user IDs and task IDs from your database
-- INSERT INTO task_timers (id, user_id, course_task_id, start_time, end_time, duration_seconds, status)
-- VALUES (
--     '33333333-3333-3333-3333-333333333333'::uuid,
--     '550e8400-e29b-41d4-a716-446655440000'::uuid,  -- user_id
--     '22222222-2222-2222-2222-222222222222'::uuid,  -- course_task_id
--     '2025-12-11 10:00:00+00'::timestamp with time zone,
--     '2025-12-11 11:30:00+00'::timestamp with time zone,
--     5400000,  -- 90 minutes in milliseconds
--     'STOPPED'
-- );
