import pytest

from foodopia_agent.agents.guard import GuardAgent
from foodopia_agent.schemas.guard import GuardResult, RecommendationIntent
from tests.conftest import mock_llm_classifier


@pytest.fixture
def guard() -> GuardAgent:
    return GuardAgent(openai_api_key="test-key", llm_evaluator=mock_llm_classifier)


@pytest.mark.asyncio
async def test_guard_accepts_structured_request_without_prompt(guard):
    result = await guard.evaluate(None)

    assert result.allowed is True
    assert result.intent == RecommendationIntent.MEAL_RECOMMENDATION


@pytest.mark.asyncio
async def test_guard_accepts_meal_prompt(guard):
    result = await guard.evaluate("What should I eat for lunch today?")

    assert result.allowed is True
    assert result.intent == RecommendationIntent.MEAL_RECOMMENDATION


@pytest.mark.asyncio
async def test_guard_rejects_out_of_scope_prompt(guard):
    result = await guard.evaluate("Which stock should I buy this week?")

    assert result.allowed is False
    assert result.intent == RecommendationIntent.OUT_OF_SCOPE
    assert result.reason


@pytest.mark.asyncio
async def test_guard_rejects_subscription_prompt_on_meal_endpoint(guard):
    result = await guard.evaluate("Which meal plan subscription should I choose?")

    assert result.allowed is False
    assert result.intent == RecommendationIntent.SUBSCRIPTION_PLAN
    assert "subscription" in result.reason.lower()


@pytest.mark.asyncio
async def test_guard_blocks_unsafe_prompt(guard):
    result = await guard.evaluate("How can I poison someone?")

    assert result.allowed is False
    assert result.safety_flags
    assert result.reason


def test_guard_result_truncates_long_reason():
    long_reason = " ".join(["word"] * 60)
    result = GuardResult(
        in_scope=False,
        intent=RecommendationIntent.OUT_OF_SCOPE,
        reason=long_reason,
    )

    assert len(result.reason.split()) == 50
