-- ============================================================================
-- COURSE MANAGEMENT SYSTEM - DATABASE SCHEMA
-- Database: PostgreSQL
-- ============================================================================

-- Drop tables if they exist (for clean setup)
DROP TABLE IF EXISTS course_tasks CASCADE;
DROP TABLE IF EXISTS courses CASCADE;

-- Drop existing functions and triggers
DROP TRIGGER IF EXISTS update_courses_updated_at ON courses CASCADE;
DROP TRIGGER IF EXISTS update_course_tasks_updated_at ON course_tasks CASCADE;
DROP FUNCTION IF EXISTS update_updated_at_column() CASCADE;

-- ============================================================================
-- COURSES TABLE
-- ============================================================================

CREATE TABLE IF NOT EXISTS courses
(
    id         UUID PRIMARY KEY,
    code       VARCHAR(50)              NOT NULL UNIQUE,
    name       VARCHAR(255)             NOT NULL,
    user_id    UUID                     NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign key to users table
    CONSTRAINT fk_course_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- ============================================================================
-- COURSE_TASKS TABLE
-- ============================================================================

CREATE TABLE IF NOT EXISTS course_tasks
(
    id          UUID PRIMARY KEY,
    name        VARCHAR(255)             NOT NULL,
    description TEXT,
    deadline    DATE,
    progress    INTEGER                  NOT NULL DEFAULT 0 CHECK (progress >= 0 AND progress <= 100),
    status      VARCHAR(50)              NOT NULL DEFAULT 'PENDING',
    course_id   UUID                     NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign key to courses table
    CONSTRAINT fk_course_task_course FOREIGN KEY (course_id) REFERENCES courses (id) ON DELETE CASCADE,

    -- Constraint on status values
    CONSTRAINT chk_course_task_status CHECK (status IN ('PENDING', 'IN_PROGRESS', 'COMPLETED'))
);

-- ============================================================================
-- INDEXES FOR PERFORMANCE
-- ============================================================================

-- Index on user_id for fast lookups of user's courses
CREATE INDEX idx_courses_user_id ON courses (user_id);

-- Index on code for unique course code lookups
CREATE INDEX idx_courses_code ON courses (code);

-- Index on course_id for fast lookups of tasks by course
CREATE INDEX idx_course_tasks_course_id ON course_tasks (course_id);

-- Index on deadline for sorting/filtering tasks by due date
CREATE INDEX idx_course_tasks_deadline ON course_tasks (deadline);

-- Index on status for filtering tasks by status
CREATE INDEX idx_course_tasks_status ON course_tasks (status);

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

-- Trigger for courses table
CREATE TRIGGER update_courses_updated_at
    BEFORE UPDATE
    ON courses
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

-- Trigger for course_tasks table
CREATE TRIGGER update_course_tasks_updated_at
    BEFORE UPDATE
    ON course_tasks
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

-- ============================================================================
-- COMMENTS (Documentation)
-- ============================================================================

COMMENT ON TABLE courses IS 'Stores course information';
COMMENT ON COLUMN courses.id IS 'Unique identifier for the course (UUID)';
COMMENT ON COLUMN courses.code IS 'Unique course code (e.g., CS101)';
COMMENT ON COLUMN courses.name IS 'Name of the course';
COMMENT ON COLUMN courses.user_id IS 'User who owns this course';
COMMENT ON COLUMN courses.created_at IS 'Timestamp when the course was created';
COMMENT ON COLUMN courses.updated_at IS 'Timestamp when the course was last updated';

COMMENT ON TABLE course_tasks IS 'Stores tasks within courses';
COMMENT ON COLUMN course_tasks.id IS 'Unique identifier for the task (UUID)';
COMMENT ON COLUMN course_tasks.name IS 'Name of the task';
COMMENT ON COLUMN course_tasks.description IS 'Detailed description of the task';
COMMENT ON COLUMN course_tasks.deadline IS 'Deadline for task completion';
COMMENT ON COLUMN course_tasks.progress IS 'Task progress (0-100)';
COMMENT ON COLUMN course_tasks.status IS 'Task status (PENDING, IN_PROGRESS, COMPLETED)';
COMMENT ON COLUMN course_tasks.course_id IS 'Course this task belongs to';
COMMENT ON COLUMN course_tasks.created_at IS 'Timestamp when the task was created';
COMMENT ON COLUMN course_tasks.updated_at IS 'Timestamp when the task was last updated';

-- ============================================================================
-- SAMPLE DATA (For Testing)
-- ============================================================================

-- Note: Replace UUIDs with actual user IDs from your users table
-- INSERT INTO courses (id, code, name, user_id)
-- VALUES ('11111111-1111-1111-1111-111111111111'::uuid, 'CS101', 'Introduction to Computer Science', '550e8400-e29b-41d4-a716-446655440000'::uuid);

-- INSERT INTO course_tasks (id, name, description, deadline, progress, status, course_id)
-- VALUES ('22222222-2222-2222-2222-222222222222'::uuid, 'Assignment 1', 'Complete programming exercises', '2025-12-31', 50, 'IN_PROGRESS', '11111111-1111-1111-1111-111111111111'::uuid);
