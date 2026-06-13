import pytest

from foodopia_agent.agents.guard import GuardAgent
from foodopia_agent.schemas.guard import GuardResult, RecommendationIntent


async def mock_llm_classifier(text: str, _expected: RecommendationIntent) -> GuardResult:
    lowered = text.lower()

    if "poison" in lowered:
        return GuardResult(
            in_scope=False,
            intent=RecommendationIntent.OUT_OF_SCOPE,
            safety_flags=["harmful_content"],
            reason="This request asks for harmful actions and cannot be processed.",
        )

    if "stock" in lowered:
        return GuardResult(
            in_scope=False,
            intent=RecommendationIntent.OUT_OF_SCOPE,
            reason="This prompt is about investing, not Foodopia meal recommendations.",
        )

    if "subscription" in lowered or "meal plan" in lowered:
        return GuardResult(
            in_scope=True,
            intent=RecommendationIntent.SUBSCRIPTION_PLAN,
            reason="This prompt is about choosing a subscription plan, not a single meal.",
        )

    return GuardResult(
        in_scope=True,
        intent=RecommendationIntent.MEAL_RECOMMENDATION,
        reason="This prompt asks for help choosing a meal.",
    )


@pytest.fixture
def guard_agent() -> GuardAgent:
    return GuardAgent(openai_api_key="test-key", llm_evaluator=mock_llm_classifier)
