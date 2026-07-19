from fastapi import APIRouter, Depends
from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession
from datetime import datetime, timezone

from ai_service.api.schemas.skills import SkillExtractionRequest, SkillExtractionResponse
from ai_service.application.services.skill_extraction_service import SkillExtractionService
from ai_service.domain.models.job_analysis import JobAnalysis
from ai_service.infrastructure.persistence.database import get_db

router = APIRouter()


@router.post("/skills/extract", response_model=SkillExtractionResponse)
async def extract_skills(
        request: SkillExtractionRequest, db: AsyncSession = Depends(get_db)
) -> SkillExtractionResponse:
    service = SkillExtractionService()
    required, nice_to_have = service.extract(request.description)

    result = await db.execute(
        select(JobAnalysis).where(JobAnalysis.job_id == request.job_id)
    )
    job_analysis = result.scalar_one_or_none()

    if job_analysis is not None:
        job_analysis.required_skills = required
        job_analysis.nice_to_have_skills = nice_to_have
        job_analysis.analyzed_at = datetime.now(timezone.utc)
    else:
        job_analysis = JobAnalysis(
            job_id=request.job_id,
            required_skills=required,
            nice_to_have_skills=nice_to_have,
        )
        db.add(job_analysis)

    await db.commit()

    return SkillExtractionResponse(
        job_id=request.job_id,
        required_skills=required,
        nice_to_have_skills=nice_to_have
    )



