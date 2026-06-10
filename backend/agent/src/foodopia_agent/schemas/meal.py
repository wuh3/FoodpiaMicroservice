"""Meal-service MCP tool return types (meal-mcp-server)."""

from __future__ import annotations

from datetime import date
from typing import Optional

from pydantic import Field

from foodopia_agent.schemas.common import JavaDto


class NutritionFactsDto(JavaDto):
    calories_kcal: float = 0.0
    protein_g: float = 0.0
    sugar_g: float = 0.0
    salt_mg: float = 0.0


class IngredientDto(JavaDto):
    id: Optional[str] = None
    name: str
    unit_price: float = Field(gt=0)
    category: str
    unit: Optional[str] = None
    nutrition_per_100g: Optional[NutritionFactsDto] = None


class DishIngredientDto(JavaDto):
    ingredient_id: str
    ingredient: Optional[IngredientDto] = None
    quantity: float = Field(gt=0)
    cost: Optional[float] = None


class DishDto(JavaDto):
    id: Optional[str] = None
    name: str
    description: Optional[str] = None
    ingredients: Optional[list[DishIngredientDto]] = None
    category: str
    serving_size: int = Field(gt=0)
    is_available: bool = False
    available_from: Optional[date] = None
    available_until: Optional[date] = None
    dietary_tags: list[str] = Field(default_factory=list)
    allergens: list[str] = Field(default_factory=list)
    image_url: Optional[str] = None
    popularity_score: float = 0.0
    times_ordered: int = 0
    total_cost: float = 0.0
    nutrition_per_serving: Optional[NutritionFactsDto] = None


class PlanLevelDto(JavaDto):
    level: int = Field(gt=0)
    meals_per_month: int = Field(gt=0)
    monthly_price: float = Field(gt=0)


class MealTemplateDto(JavaDto):
    id: Optional[str] = None
    name: str
    dish_categories: dict[str, int] = Field(default_factory=dict)
    total_dishes: int = Field(gt=0)
    required_tags: list[str] = Field(default_factory=list)
    forbidden_tags: list[str] = Field(default_factory=list)


class MealPlanTypeDto(JavaDto):
    id: Optional[str] = None
    plan_code: str
    display_name: str
    description: Optional[str] = None
    template_id: str
    levels: list[PlanLevelDto] = Field(min_length=1)
    is_active: bool = True
