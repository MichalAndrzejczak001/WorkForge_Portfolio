import pytest

from ai_service.application.services.skill_extraction_service import SkillExtractionService


@pytest.fixture(scope="module")
def service() -> SkillExtractionService:
    return SkillExtractionService()


def test_extracts_required_skills_only(service: SkillExtractionService) -> None:
    description = "Szukamy programisty Java ze znajomością Spring Boot i Docker."

    required, nice_to_have = service.extract(description)

    assert required == ["Docker", "Java", "Spring Boot"]
    assert nice_to_have == []


def test_splits_required_and_nice_to_have_pl(service: SkillExtractionService) -> None:
    description = (
        "Wymagana znajomość Kafka oraz PostgreSQL. "
        "Mile widziana znajomość Kubernetes i AWS."
    )

    required, nice_to_have = service.extract(description)

    assert required == ["Kafka", "PostgreSQL"]
    assert nice_to_have == ["AWS", "Kubernetes"]


def test_splits_required_and_nice_to_have_en(service: SkillExtractionService) -> None:
    description = "Java developer, Spring Security, REST API. Nice to have: Kubernetes, GraphQL."

    required, nice_to_have = service.extract(description)

    assert required == ["Java", "REST API", "Spring Security"]
    assert nice_to_have == ["GraphQL", "Kubernetes"]


def test_prefers_longest_overlapping_match(service: SkillExtractionService) -> None:
    description = "Wymagana znajomość Spring Boot oraz Spring Security."

    required, _ = service.extract(description)

    assert required == ["Spring Boot", "Spring Security"]
    assert "Spring" not in required


def test_returns_empty_lists_when_no_known_skills_found(service: SkillExtractionService) -> None:
    required, nice_to_have = service.extract("Szukamy osoby komunikatywnej i zorganizowanej.")

    assert required == []
    assert nice_to_have == []


def test_matching_is_case_insensitive(service: SkillExtractionService) -> None:
    required, _ = service.extract("wymagana znajomość java, spring boot, docker")

    assert required == ["Docker", "Java", "Spring Boot"]
