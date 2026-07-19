from ai_service.config import settings
from openai import OpenAI


class SummaryGenerator:

    def __init__(self) -> None:
        if settings.openai_api_key:
            self._client = OpenAI(api_key=settings.openai_api_key)
        else:
            self._client = None

    def _generate_template(
            self, score: float, matched_skills: list[str], missing_skills: list[str]
    ) -> str:
        percent = round(score * 100, 2)
        matched_part = ", ".join(matched_skills) if matched_skills else "brak"
        missing_part = ", ".join(missing_skills) if missing_skills else "brak"
        return (
            f"Dopasowanie: {percent}%. "
            f"Pasujące umiejętności: {matched_part}. "
            f"Brakujące umiejętności: {missing_part}."
        )

    def generate(
            self, score: float, matched_skills: list[str], missing_skills: list[str]
    ) -> str:
        if self._client is None:
            return self._generate_template(score, matched_skills, missing_skills)

        prompt = (
            f"Dopasowanie kandydata do oferty: {round(score * 100, 2)}%. "
            f"Pasujące umiejętności: {', '.join(matched_skills) or 'brak'}. "
            f"Brakujące umiejętności: {', '.join(missing_skills) or 'brak'}. "
            f"Napisz krótkie (1-2 zdania) podsumowanie dopasowania kandydata po polsku."
            
        )
        try:
            response = self._client.chat.completions.create(
                model=settings.openai_model,
                messages=[{"role": "user", "content": prompt}],
                timeout=5
            )
            return response.choices[0].message.content
        except Exception:
            return self._generate_template(score, matched_skills, missing_skills)
