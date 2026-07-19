import json
import re
from pathlib import Path

import spacy
from spacy.matcher import PhraseMatcher
from spacy.util import filter_spans

_SKILLS_DICTIONARY_PATH = (
    Path(__file__).resolve().parents[2]
    / "infrastructure"
    / "nlp"
    / "skills_dictionary.json"
)

_NICE_TO_HAVE_MARKER_PATTERNS = [
    r"mile\s+widzian\w*",
    r"dodatkow\w*\s+atut\w*",
    r"będzie\s+atut\w*",
    r"nice[\s-]+to[\s-]+have",
    r"would\s+be\s+a?\s*plus",
    r"is\s+a\s+plus",
    r"preferred",
    r"bonus\s+points",
]

_NICE_TO_HAVE_PATTERN = re.compile(
    "|".join(_NICE_TO_HAVE_MARKER_PATTERNS), re.IGNORECASE
)


class SkillExtractionService:
    """Ekstrakcja umiejętności z opisu oferty przez dopasowanie fraz ze słownika (spaCy PhraseMatcher)."""

    def __init__(self, skills_dictionary_path: Path = _SKILLS_DICTIONARY_PATH) -> None:
        self._nlp = spacy.blank("xx")
        self._skills_by_lower = self._load_skills(skills_dictionary_path)

        self._matcher = PhraseMatcher(self._nlp.vocab, attr="LOWER")
        patterns = [
            self._nlp.make_doc(skill) for skill in self._skills_by_lower.values()
        ]
        self._matcher.add("SKILL", patterns)

    def _load_skills(self, path: Path) -> dict[str, str]:
        with open(path, encoding="utf-8") as f:
            skills: list[str] = json.load(f)
        return {skill.lower(): skill for skill in skills}

    def _extract_from_text(self, text: str) -> set[str]:
        doc = self._nlp(text)
        matches = self._matcher(doc)
        spans = [doc[start:end] for _, start, end in matches]
        longest_spans = filter_spans(spans)

        found = set()
        for span in longest_spans:
            canonical = self._skills_by_lower.get(span.text.lower())
            if canonical:
                found.add(canonical)
        return found

    def extract(self, description: str) -> tuple[list[str], list[str]]:
        """Zwraca (required_skills, nice_to_have_skills) wyekstrahowane z opisu oferty.

        Sekcja po pierwszym wystąpieniu markera typu "mile widziane" / "nice to have"
        traktowana jest jako umiejętności dodatkowe; wcześniejsza część jako wymagane.
        """
        marker_match = _NICE_TO_HAVE_PATTERN.search(description)

        if marker_match is None:
            required = self._extract_from_text(description)
            return sorted(required), []

        required_text = description[: marker_match.start()]
        nice_to_have_text = description[marker_match.start() :]

        required = self._extract_from_text(required_text)
        nice_to_have = self._extract_from_text(nice_to_have_text) - required

        return sorted(required), sorted(nice_to_have)
