import pytest

from ai_service.application.services.matching_service import MatchingService


@pytest.fixture
def service() -> MatchingService:
    return MatchingService()


def test_calculates_partial_match(service: MatchingService) -> None:
    result = service.calculate(
        required_skills=["Java", "Spring Boot", "Docker", "PostgreSQL"],
        candidate_skills=["Java", "Docker", "Kubernetes"],
    )

    assert result.score == 0.4
    assert result.matched_skills == ["Docker", "Java"]
    assert result.missing_skills == ["PostgreSQL", "Spring Boot"]


def test_calculates_full_match(service: MatchingService) -> None:
    result = service.calculate(
        required_skills=["Java", "Docker"], candidate_skills=["Java", "Docker"]
    )

    assert result.score == 1
    assert result.matched_skills == ["Docker", "Java"]
    assert result.missing_skills == []


def test_calculates_zero_match_when_no_overlap(service: MatchingService) -> None:
    result = service.calculate(
        required_skills=["Java", "Docker"], candidate_skills=["Python", "Kubernetes"]
    )

    assert result.score == 0
    assert result.matched_skills == []
    assert result.missing_skills == ["Docker", "Java"]


def test_returns_zero_when_both_lists_empty(service: MatchingService) -> None:
    result = service.calculate(required_skills=[], candidate_skills=[])

    assert result.score == 0
    assert result.matched_skills == []
    assert result.missing_skills == []
