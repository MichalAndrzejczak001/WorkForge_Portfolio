UPDATE job_offers SET status = 'ARCHIVED' WHERE status = 'CLOSED';

ALTER TABLE job_offers
    MODIFY COLUMN status ENUM('DRAFT', 'PUBLISHED', 'EXPIRED', 'ARCHIVED'),
    ADD COLUMN company_name VARCHAR(255),
    ADD COLUMN work_mode ENUM('REMOTE', 'HYBRID', 'ONSITE'),
    ADD COLUMN experience_level ENUM('JUNIOR', 'MID', 'SENIOR'),
    ADD COLUMN published_at DATETIME(6),
    ADD COLUMN expires_at DATETIME(6);

CREATE TABLE job_offer_skills (
    job_offer_id BINARY(16) NOT NULL,
    skill VARCHAR(255),
    FOREIGN KEY (job_offer_id) REFERENCES job_offers(id)
);
