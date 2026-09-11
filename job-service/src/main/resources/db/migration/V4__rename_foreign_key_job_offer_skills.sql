ALTER TABLE job_offer_skills
    DROP FOREIGN KEY job_offer_skills_ibfk_1,
    ADD CONSTRAINT fk_job_offer_skills_job_offer FOREIGN KEY (job_offer_id) REFERENCES job_offers(id);