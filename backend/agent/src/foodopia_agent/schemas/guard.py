"""Guard agent classification result."""

from __future__ import annotations

from enum import Enum

from pydantic import Field, field_validator

from foodopia_agent.schemas.common import JavaDto

MAX_REASON_WORDS = 50


class RecommendationIntent(str, Enum):
    MEAL_RECOMMENDATION = "meal_recommendation"
    SUBSCRIPTION_PLAN = "subscription_plan"
    OUT_OF_SCOPE = "out_of_scope"


class GuardResult(JavaDto):
    in_scope: bool = True
    intent: RecommendationIntent = RecommendationIntent.MEAL_RECOMMENDATION
    safety_flags: list[str] = Field(default_factory=list)
    reason: str = Field(
        default="",
        description="Short explanation from the guard agent, max 50 words",
    )

    @field_validator("reason")
    @classmethod
    def limit_reason_length(cls, value: str) -> str:
        words = value.split()
        if len(words) <= MAX_REASON_WORDS:
            return value
        return " ".join(words[:MAX_REASON_WORDS])

    @property
    def allowed(self) -> bool:
        return (
            self.in_scope
            and not self.safety_flags
            and self.intent == RecommendationIntent.MEAL_RECOMMENDATION
        )
