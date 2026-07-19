import uuid
from datetime import datetime, timezone

from sqlalchemy import ARRAY, DateTime, String
from sqlalchemy.dialects.postgresql import UUID
from sqlalchemy.orm import DeclarativeBase, Mapped, mapped_column


class Base(DeclarativeBase):
    pass


class JobAnalysis(Base):
    __tablename__ = "job_analysis"

    id: Mapped[uuid.UUID] = mapped_column(
        UUID(as_uuid=True), primary_key=True, default=uuid.uuid4
    )
    job_id: Mapped[uuid.UUID] = mapped_column(
        UUID(as_uuid=True), nullable=False, unique=True
    )
    required_skills: Mapped[list[str]] = mapped_column(
        ARRAY(String), nullable=False, default=list
    )
    nice_to_have_skills: Mapped[list[str]] = mapped_column(
        ARRAY(String), nullable=False, default=list
    )
    analyzed_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True),
        nullable=False,
        default=lambda: datetime.now(timezone.utc),
    )
