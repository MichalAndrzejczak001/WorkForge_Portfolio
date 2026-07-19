from uuid import UUID
from pydantic import BaseModel, ConfigDict


class MatchRequest(BaseModel):
    job_id: UUID
    candidate_id: UUID
    candidate_skills: list[str]


class MatchResponse(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    job_id: UUID
    candidate_id: UUID
    score: float
    matched_skills: list[str]
    missing_skills: list[str]
    summary: str
