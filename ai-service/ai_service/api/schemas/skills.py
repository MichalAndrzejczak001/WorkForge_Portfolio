from uuid import UUID
from pydantic import BaseModel, ConfigDict


class SkillExtractionRequest(BaseModel):
    job_id: UUID
    description: str


class SkillExtractionResponse(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    job_id: UUID
    required_skills: list[str]
    nice_to_have_skills: list[str]
