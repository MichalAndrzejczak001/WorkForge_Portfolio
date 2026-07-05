CREATE TABLE applications
(
    id           BINARY(16) NOT NULL,
    job_id       BINARY(16) NOT NULL,
    applicant_id BINARY(16) NOT NULL,
    status       ENUM('PENDING', 'REVIEWED', 'ACCEPTED', 'REJECTED') NOT NULL,
    applied_at   DATETIME(6) NOT NULL,
    updated_at   DATETIME(6),
    PRIMARY KEY (id)
)