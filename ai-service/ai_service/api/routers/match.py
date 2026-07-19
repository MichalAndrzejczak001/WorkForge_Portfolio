from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from ai_service.api.schemas.match import MatchRequest, MatchResponse
from ai_service.application.services.matching_service import MatchingService
from ai_service.application.services.summary_generator import SummaryGenerator
from ai_service.domain.models.job_analysis import JobAnalysis
from ai_service.domain.models.match_result import MatchResult
from ai_service.infrastructure.persistence.database import get_db


router = APIRouter()

@router.post("/match", response_model=MatchResponse)
async def match_candidate(
        request: MatchRequest, db: AsyncSession = Depends(get_db)
) -> MatchResponse:
    result = await db.execute(
        select(JobAnalysis).where(JobAnalysis.job_id == request.job_id)
    )
    job_analysis = result.scalar_one_or_none()

    if job_analysis is None:
        raise HTTPException(
            status_code=404,
            detail=f'No job analysis found for job_id {request.job_id}'
        )

    matching_service = MatchingService()
    match_score = matching_service.calculate(
        job_analysis.required_skills, request.candidate_skills
    )

    summary_generator = SummaryGenerator()
    summary = summary_generator.generate(
        match_score.score, match_score.matched_skills, match_score.missing_skills
    )

    match_result = MatchResult(
        job_id=request.job_id,
        candidate_id=request.candidate_id,
        score=match_score.score,
        matched_skills=match_score.matched_skills,
        missing_skills=match_score.missing_skills,
        summary=summary,
    )
    db.add(match_result)
    await db.commit()

    return MatchResponse(
        job_id=request.job_id,
        candidate_id=request.candidate_id,
        score=match_score.score,
        matched_skills=match_score.matched_skills,
        missing_skills=match_score.missing_skills,
        summary=summary
    )
