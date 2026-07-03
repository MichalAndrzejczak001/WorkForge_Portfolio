CREATE TABLE job_offers (
    id BINARY(16) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    location VARCHAR(255),
    salary_min DECIMAL(10,2),
    salary_max DECIMAL(10,2),
    status ENUM('DRAFT', 'PUBLISHED', 'CLOSED'),
    recruiter_id BINARY(16),
    created_at DATETIME(6),
    updated_at DATETIME(6),
    closed_at DATETIME(6),
    PRIMARY KEY (id)
);