ALTER TABLE applications
    ADD CONSTRAINT uq_applications_job_applicant UNIQUE (job_id, applicant_id);