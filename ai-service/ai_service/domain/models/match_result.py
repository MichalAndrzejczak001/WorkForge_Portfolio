import uuid
from datetime import datetime, timezone

from sqlalchemy import ARRAY, DateTime, Float, String
from sqlalchemy.dialects.postgresql import UUID
from sqlalchemy.orm import Mapped, mapped_column

from ai_service.domain.models.job_analysis import Base


class MatchResult(Base):
    __tablename__ = "match_result"

    id: Mapped[uuid.UUID] = mapped_column(
        UUID(as_uuid=True), primary_key=True, default=uuid.uuid4
    )
    job_id: Mapped[uuid.UUID] = mapped_column(UUID(as_uuid=True), nullable=False)
    candidate_id: Mapped[uuid.UUID] = mapped_column(UUID(as_uuid=True), nullable=False)
    score: Mapped[float] = mapped_column(Float, nullable=False)
    matched_skills: Mapped[list[str]] = mapped_column(
        ARRAY(String), nullable=False, default=list
    )
    missing_skills: Mapped[list[str]] = mapped_column(
        ARRAY(String), nullable=False, default=list
    )
    summary: Mapped[str] = mapped_column(String, nullable=False)
    created_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True),
        nullable=False,
        default=lambda: datetime.now(timezone.utc),
    )
