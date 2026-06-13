"""Step 1 — API contract for meal recommendation.

The HTTP layer speaks in request/response models. The graph uses separate *state*
(see state/meal_recommendation_state.py). Keeping them separate lets you change
the workflow without breaking the public API.
"""

from __future__ import annotations

from typing import Optional

from pydantic import Field

from foodopia_agent.schemas.common import JavaDto
from foodopia_agent.schemas.meal import DishDto, NutritionFactsDto


class NutritionTargets(JavaDto):
    """Optional per-meal nutrition goals from the client."""

    max_calories_kcal: Optional[float] = Field(default=None, gt=0)
    min_protein_g: Optional[float] = Field(default=None, ge=0)


class MealPreferenceOverrides(JavaDto):
    """Per-request meal constraints merged with (not replacing) profile preferences."""

    ingredients_to_avoid: list[str] = Field(default_factory=list)
    ingredients_required: list[str] = Field(default_factory=list)


class MealRecommendationRequest(JavaDto):
    user_id: str = Field(min_length=1)
    prompt: Optional[str] = Field(
        default=None,
        description="Optional natural-language request evaluated by the guard agent",
    )
    nutrition_targets: Optional[NutritionTargets] = None
    meal_preferences: Optional[MealPreferenceOverrides] = None


class RecommendedMealSummary(JavaDto):
    dishes: list[DishDto]
    nutrition_totals: NutritionFactsDto
    is_valid: bool
    rationale: str
    plan_code: str
    plan_level: int
    template_name: str
