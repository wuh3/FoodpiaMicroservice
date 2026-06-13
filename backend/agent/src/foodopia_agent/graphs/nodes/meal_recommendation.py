"""Step 3 — Graph nodes: one responsibility per function.

Each node maps to a step in Use Case 8:
  load_context      → profile + subscription + template
  fetch_candidates  → list dishes, filter by template tags
  filter_nutrition  → optional calorie/protein targets
  assemble_meal     → pick dishes per category
  validate_meal     → MCP validate_meal_template_dishes
  finalize          → build rationale string
"""

from __future__ import annotations

from foodopia_agent.schemas.customer import UserProfileDto
from foodopia_agent.schemas.meal import DishDto, MealTemplateDto
from foodopia_agent.schemas.subscription import MealPlanSubscriptionContext
from foodopia_agent.services.dish_selection import (
    compute_nutrition_totals,
    filter_by_nutrition_targets,
    filter_candidates,
    select_meal_for_template,
)
from foodopia_agent.state.meal_recommendation_state import MealRecommendationState
from foodopia_agent.tools.mcp_tools import FoodopiaMcpTools


def make_meal_recommendation_nodes(tools: FoodopiaMcpTools):
    """Factory: closes over MCP tools so nodes can call meal/customer services."""

    async def load_context(state: MealRecommendationState) -> MealRecommendationState:
        user_id = state["user_id"]
        try:
            profile = await tools.get_user_profile(user_id)
            context = await tools.resolve_subscription_context(user_id)
        except Exception as exc:
            return {"error": str(exc)}

        return {
            "user_profile": profile.model_dump(by_alias=True),
            "subscription_context": context.model_dump(by_alias=True),
            "error": None,
        }

    async def fetch_candidates(state: MealRecommendationState) -> MealRecommendationState:
        if state.get("error"):
            return {}

        context = MealPlanSubscriptionContext.model_validate(state["subscription_context"])
        profile = UserProfileDto.model_validate(state["user_profile"])
        template = context.template

        dishes = await tools.list_dishes()
        candidates = filter_candidates(
            dishes,
            template,
            profile,
            ingredients_to_avoid=state.get("ingredients_to_avoid"),
        )

        return {
            "candidate_dishes": [dish.model_dump(by_alias=True) for dish in candidates],
        }

    async def filter_nutrition(state: MealRecommendationState) -> MealRecommendationState:
        if state.get("error"):
            return {}

        context = MealPlanSubscriptionContext.model_validate(state["subscription_context"])
        template = context.template
        candidates = [DishDto.model_validate(item) for item in state.get("candidate_dishes", [])]

        filtered = filter_by_nutrition_targets(
            candidates,
            template,
            state.get("max_calories_kcal"),
            state.get("min_protein_g"),
        )

        return {
            "candidate_dishes": [dish.model_dump(by_alias=True) for dish in filtered],
        }

    async def assemble_meal(state: MealRecommendationState) -> MealRecommendationState:
        if state.get("error"):
            return {}

        context = MealPlanSubscriptionContext.model_validate(state["subscription_context"])
        template = context.template
        candidates = [DishDto.model_validate(item) for item in state.get("candidate_dishes", [])]

        try:
            selected = select_meal_for_template(
                candidates,
                template,
                ingredients_required=state.get("ingredients_required"),
            )
        except ValueError as exc:
            return {"error": str(exc), "selected_dishes": [], "is_valid": False}

        return {
            "selected_dishes": [dish.model_dump(by_alias=True) for dish in selected],
        }

    async def validate_meal(state: MealRecommendationState) -> MealRecommendationState:
        if state.get("error"):
            return {}

        context = MealPlanSubscriptionContext.model_validate(state["subscription_context"])
        template = context.template
        selected = [DishDto.model_validate(item) for item in state.get("selected_dishes", [])]
        dish_ids = [dish.id for dish in selected if dish.id]

        if not dish_ids:
            return {"is_valid": False, "error": "No dish IDs available for validation"}

        is_valid = await tools.validate_meal_template_dishes(template.id or "", dish_ids)
        return {"is_valid": is_valid}

    async def finalize(state: MealRecommendationState) -> MealRecommendationState:
        if state.get("error"):
            return {
                "rationale": f"Could not build a meal recommendation: {state['error']}",
                "is_valid": False,
            }

        context = MealPlanSubscriptionContext.model_validate(state["subscription_context"])
        selected = [DishDto.model_validate(item) for item in state.get("selected_dishes", [])]
        totals = compute_nutrition_totals(selected)
        is_valid = state.get("is_valid", False)

        plan = context.plan_type.display_name
        level = context.subscription.plan_level
        template_name = context.template.name
        dish_names = ", ".join(dish.name for dish in selected)

        if is_valid:
            rationale = (
                f"Selected {len(selected)} dishes for {plan} (level {level}) "
                f"using template '{template_name}': {dish_names}. "
                f"Total nutrition ≈ {totals.calories_kcal:.0f} kcal, "
                f"{totals.protein_g:.1f} g protein."
            )
        else:
            rationale = (
                f"Proposed dishes ({dish_names}) did not pass template validation "
                f"for '{template_name}'."
            )

        return {"rationale": rationale}

    return {
        "load_context": load_context,
        "fetch_candidates": fetch_candidates,
        "filter_nutrition": filter_nutrition,
        "assemble_meal": assemble_meal,
        "validate_meal": validate_meal,
        "finalize": finalize,
    }


def route_after_load(state: MealRecommendationState) -> str:
    """Conditional edge: skip pipeline when load_context failed."""
    return "finalize" if state.get("error") else "fetch_candidates"


def route_after_assemble(state: MealRecommendationState) -> str:
    return "finalize" if state.get("error") else "validate_meal"
