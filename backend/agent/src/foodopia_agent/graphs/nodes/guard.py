"""Guard graph node — first step in Use Case 8."""

from __future__ import annotations

from foodopia_agent.agents.guard import GuardAgent
from foodopia_agent.schemas.guard import RecommendationIntent
from foodopia_agent.state.meal_recommendation_state import MealRecommendationState


def make_guard_node(guard_agent: GuardAgent):
    async def guard_request(state: MealRecommendationState) -> MealRecommendationState:
        result = await guard_agent.evaluate(
            state.get("prompt"),
            expected_intent=RecommendationIntent.MEAL_RECOMMENDATION,
        )

        update: MealRecommendationState = {
            "guard_result": result.model_dump(by_alias=True),
            "guard_blocked": not result.allowed,
        }

        if result.allowed:
            update["error"] = None
            return update

        update["error"] = result.reason or "Request blocked by guard agent"
        update["is_valid"] = False
        update["rationale"] = update["error"]
        return update

    return guard_request


def route_after_guard(state: MealRecommendationState) -> str:
    return "finalize" if state.get("guard_blocked") else "load_context"
