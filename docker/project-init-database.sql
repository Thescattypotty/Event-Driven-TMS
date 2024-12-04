\c project;

CREATE TABLE projects (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255),
    description TEXT,
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    project_users TEXT[],
    project_tasks TEXT[],
    project_files TEXT[]
);

-- Optional: Create indexes for performance
CREATE INDEX idx_project_name ON projects(name);
CREATE INDEX idx_project_start_date ON projects(start_date);
CREATE INDEX idx_project_end_date ON projects(end_date);