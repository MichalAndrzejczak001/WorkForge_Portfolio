CREATE TABLE job_status_cache (
    job_id BINARY(16) NOT NULL,
    status ENUM('PUBLISHED', 'ARCHIVED', 'EXPIRED') NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (job_id)
)