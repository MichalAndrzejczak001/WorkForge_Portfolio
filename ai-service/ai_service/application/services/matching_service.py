from dataclasses import dataclass


@dataclass
class MatchScore:
    score: float
    matched_skills: list[str]
    missing_skills: list[str]


class MatchingService:
    def calculate(
        self, required_skills: list[str], candidate_skills: list[str]
    ) -> MatchScore:
        required = set(required_skills)
        candidate = set(candidate_skills)

        matched = sorted(required & candidate)
        missing = sorted(required - candidate)

        union = required | candidate
        score = len(required & candidate) / len(union) if union else 0.0

        return MatchScore(score=score, matched_skills=matched, missing_skills=missing)
