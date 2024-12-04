\c task;

CREATE TABLE tasks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(255),
    description TEXT,
    due_date TIMESTAMP,
    priority VARCHAR(50),
    status VARCHAR(50),
    project_id VARCHAR(255),
    history_of_task TEXT[],
    files_included TEXT[],
    user_assigned VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Optional: Create indexes for performance
CREATE INDEX idx_task_project_id ON tasks(project_id);
CREATE INDEX idx_task_user_assigned ON tasks(user_assigned);
CREATE INDEX idx_task_status ON tasks(status);
CREATE INDEX idx_task_priority ON tasks(priority);