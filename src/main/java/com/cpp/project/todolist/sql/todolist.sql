-- ============================================================================
-- TODO LIST MANAGEMENT SYSTEM - DATABASE SCHEMA
-- Database: PostgreSQL
-- ============================================================================

-- Drop tables if they exist (for clean setup)
DROP TABLE IF EXISTS todo_list_tasks CASCADE;
DROP TABLE IF EXISTS todo_lists CASCADE;

-- Drop existing functions and triggers
DROP TRIGGER IF EXISTS update_todo_lists_updated_at ON todo_lists;
DROP TRIGGER IF EXISTS update_todo_list_tasks_updated_at ON todo_list_tasks;

-- ============================================================================
-- TODO_LISTS TABLE
-- ============================================================================

CREATE TABLE IF NOT EXISTS todo_lists
(
    id         UUID PRIMARY KEY,
    name       VARCHAR(255)             NOT NULL,
    user_id    UUID                     NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign key to users table
    CONSTRAINT fk_todo_list_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ============================================================================
-- TODO_LIST_TASKS TABLE
-- ============================================================================

CREATE TABLE IF NOT EXISTS todo_list_tasks
(
    id           UUID PRIMARY KEY,
    description  TEXT                     NOT NULL,
    deadline     DATE,
    status       VARCHAR(50)              NOT NULL DEFAULT 'PENDING',
    todo_list_id UUID                     NOT NULL,
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign key to todo_lists table
    CONSTRAINT fk_todo_list_task_list FOREIGN KEY (todo_list_id) REFERENCES todo_lists(id) ON DELETE CASCADE,

    -- Constraint on status values
    CONSTRAINT chk_todo_list_task_status CHECK (status IN ('PENDING', 'IN_PROGRESS', 'COMPLETED'))
);

-- ============================================================================
-- INDEXES FOR PERFORMANCE
-- ============================================================================

-- Index on user_id for fast lookups of user's todo lists
CREATE INDEX idx_todo_lists_user_id ON todo_lists (user_id);

-- Index on todo_list_id for fast lookups of tasks by list
CREATE INDEX idx_todo_list_tasks_list_id ON todo_list_tasks (todo_list_id);

-- Index on deadline for sorting/filtering tasks by due date (for calendar aggregation)
CREATE INDEX idx_todo_list_tasks_deadline ON todo_list_tasks (deadline);

-- Index on status for filtering tasks by status
CREATE INDEX idx_todo_list_tasks_status ON todo_list_tasks (status);

-- ============================================================================
-- TRIGGERS FOR UPDATED_AT TIMESTAMP
-- ============================================================================

-- Trigger for todo_lists table (reuses function from course.sql)
CREATE TRIGGER update_todo_lists_updated_at
    BEFORE UPDATE
    ON todo_lists
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

-- Trigger for todo_list_tasks table
CREATE TRIGGER update_todo_list_tasks_updated_at
    BEFORE UPDATE
    ON todo_list_tasks
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

-- ============================================================================
-- COMMENTS (Documentation)
-- ============================================================================

COMMENT ON TABLE todo_lists IS 'Stores todo list information';
COMMENT ON COLUMN todo_lists.id IS 'Unique identifier for the todo list (UUID)';
COMMENT ON COLUMN todo_lists.name IS 'Name of the todo list';
COMMENT ON COLUMN todo_lists.user_id IS 'User who owns this todo list';
COMMENT ON COLUMN todo_lists.created_at IS 'Timestamp when the list was created';
COMMENT ON COLUMN todo_lists.updated_at IS 'Timestamp when the list was last updated';

COMMENT ON TABLE todo_list_tasks IS 'Stores tasks within todo lists';
COMMENT ON COLUMN todo_list_tasks.id IS 'Unique identifier for the task (UUID)';
COMMENT ON COLUMN todo_list_tasks.description IS 'Description of the task';
COMMENT ON COLUMN todo_list_tasks.deadline IS 'Deadline for task completion';
COMMENT ON COLUMN todo_list_tasks.status IS 'Task status (PENDING, IN_PROGRESS, COMPLETED)';
COMMENT ON COLUMN todo_list_tasks.todo_list_id IS 'Todo list this task belongs to';
COMMENT ON COLUMN todo_list_tasks.created_at IS 'Timestamp when the task was created';
COMMENT ON COLUMN todo_list_tasks.updated_at IS 'Timestamp when the task was last updated';

-- ============================================================================
-- SAMPLE DATA (For Testing)
-- ============================================================================

-- Note: Replace UUIDs with actual user IDs from your users table
-- INSERT INTO todo_lists (id, name, user_id)
-- VALUES ('33333333-3333-3333-3333-333333333333'::uuid, 'Personal Tasks', '550e8400-e29b-41d4-a716-446655440000'::uuid);

-- INSERT INTO todo_list_tasks (id, description, deadline, status, todo_list_id)
-- VALUES ('44444444-4444-4444-4444-444444444444'::uuid, 'Buy groceries', '2025-12-01', 'PENDING', '33333333-3333-3333-3333-333333333333'::uuid);
