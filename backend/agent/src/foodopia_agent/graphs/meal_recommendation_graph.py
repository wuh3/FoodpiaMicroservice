from __future__ import annotations

from langgraph.graph import END, START, StateGraph

from foodopia_agent.agents.guard import GuardAgent
from foodopia_agent.config import Settings, get_settings
from foodopia_agent.exceptions.guard import GuardRejectedError
from foodopia_agent.graphs.nodes.guard import make_guard_node, route_after_guard
from foodopia_agent.graphs.nodes.meal_recommendation import (
    make_meal_recommendation_nodes,
    route_after_assemble,
    route_after_load,
)
from foodopia_agent.schemas.guard import GuardResult
from foodopia_agent.schemas.meal import DishDto
from foodopia_agent.schemas.recommendation import (
    MealPreferenceOverrides,
    MealRecommendationRequest,
    NutritionTargets,
    RecommendedMealSummary,
)
from foodopia_agent.schemas.subscription import MealPlanSubscriptionContext
from foodopia_agent.services.dish_selection import compute_nutrition_totals
from foodopia_agent.state.meal_recommendation_state import MealRecommendationState
from foodopia_agent.tools.mcp_tools import FoodopiaMcpTools


def build_meal_recommendation_graph(
    tools: FoodopiaMcpTools,
    guard_agent: GuardAgent | None = None,
):
    guard_agent = guard_agent or GuardAgent.from_settings(get_settings())
    meal_nodes = make_meal_recommendation_nodes(tools)

    graph = StateGraph(MealRecommendationState)
    graph.add_node("guard_request", make_guard_node(guard_agent))
    graph.add_node("load_context", meal_nodes["load_context"])
    graph.add_node("fetch_candidates", meal_nodes["fetch_candidates"])
    graph.add_node("filter_nutrition", meal_nodes["filter_nutrition"])
    graph.add_node("assemble_meal", meal_nodes["assemble_meal"])
    graph.add_node("validate_meal", meal_nodes["validate_meal"])
    graph.add_node("finalize", meal_nodes["finalize"])

    graph.add_edge(START, "guard_request")
    graph.add_conditional_edges("guard_request", route_after_guard)
    graph.add_conditional_edges("load_context", route_after_load)
    graph.add_edge("fetch_candidates", "filter_nutrition")
    graph.add_edge("filter_nutrition", "assemble_meal")
    graph.add_conditional_edges("assemble_meal", route_after_assemble)
    graph.add_edge("validate_meal", "finalize")
    graph.add_edge("finalize", END)

    return graph.compile()


async def run_meal_recommendation(
    tools: FoodopiaMcpTools,
    request: MealRecommendationRequest,
    guard_agent: GuardAgent | None = None,
    settings: Settings | None = None,
) -> RecommendedMealSummary:
    settings = settings or get_settings()
    guard_agent = guard_agent or GuardAgent.from_settings(settings)
    graph = build_meal_recommendation_graph(tools, guard_agent)

    targets: NutritionTargets | None = request.nutrition_targets
    preferences: MealPreferenceOverrides | None = request.meal_preferences
    initial_state: MealRecommendationState = {
        "user_id": request.user_id,
        "prompt": request.prompt,
        "max_calories_kcal": targets.max_calories_kcal if targets else None,
        "min_protein_g": targets.min_protein_g if targets else None,
        "ingredients_to_avoid": list(preferences.ingredients_to_avoid) if preferences else [],
        "ingredients_required": list(preferences.ingredients_required) if preferences else [],
        "guard_result": None,
        "guard_blocked": False,
        "candidate_dishes": [],
        "selected_dishes": [],
        "is_valid": False,
        "rationale": "",
        "error": None,
    }

    final_state = await graph.ainvoke(initial_state)

    if final_state.get("guard_blocked"):
        guard_data = final_state.get("guard_result") or {}
        raise GuardRejectedError(GuardResult.model_validate(guard_data))

    context_data = final_state.get("subscription_context") or {}
    context = (
        MealPlanSubscriptionContext.model_validate(context_data)
        if context_data
        else None
    )

    selected = [DishDto.model_validate(item) for item in final_state.get("selected_dishes", [])]
    totals = compute_nutrition_totals(selected)

    return RecommendedMealSummary(
        dishes=selected,
        nutrition_totals=totals,
        is_valid=final_state.get("is_valid", False),
        rationale=final_state.get("rationale", ""),
        plan_code=context.subscription.plan_code if context else "",
        plan_level=context.subscription.plan_level if context else 0,
        template_name=context.template.name if context else "",
    )
