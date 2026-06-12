"""Typed wrappers around Foodopia MCP tools."""

from __future__ import annotations

from typing import Any, TypeVar

from pydantic import BaseModel

from foodopia_agent.clients.mcp import FoodopiaMcpGateway
from foodopia_agent.exceptions.mcp import McpToolError
from foodopia_agent.schemas.customer import UserProfileDto, UserSubscriptionDto
from foodopia_agent.schemas.meal import DishDto, IngredientDto, MealPlanTypeDto, MealTemplateDto
from foodopia_agent.schemas.subscription import MealPlanSubscriptionContext

TModel = TypeVar("TModel", bound=BaseModel)


class FoodopiaMcpTools:
    """Typed MCP tool facade used by agent workflows."""

    def __init__(self, gateway: FoodopiaMcpGateway):
        self._gateway = gateway

    # --- meal-service tools ---

    async def get_meal_plan_type(self, plan_code: str) -> MealPlanTypeDto:
        payload = await self._gateway.meal.call_tool(
            "get_meal_plan_type",
            {"planCode": plan_code},
        )
        return self._parse_model(MealPlanTypeDto, payload, "get_meal_plan_type")

    async def list_active_meal_plan_types(self) -> list[MealPlanTypeDto]:
        payload = await self._gateway.meal.call_tool("list_active_meal_plan_types")
        return self._parse_model_list(MealPlanTypeDto, payload, "list_active_meal_plan_types")

    async def get_meal_template(self, template_id: str) -> MealTemplateDto:
        payload = await self._gateway.meal.call_tool(
            "get_meal_template",
            {"templateId": template_id},
        )
        return self._parse_model(MealTemplateDto, payload, "get_meal_template")

    async def list_meal_templates(self) -> list[MealTemplateDto]:
        payload = await self._gateway.meal.call_tool("list_meal_templates")
        return self._parse_model_list(MealTemplateDto, payload, "list_meal_templates")

    async def validate_meal_template_dishes(
        self,
        template_id: str,
        dish_ids: list[str],
    ) -> bool:
        payload = await self._gateway.meal.call_tool(
            "validate_meal_template_dishes",
            {"templateId": template_id, "dishIds": dish_ids},
        )
        if isinstance(payload, bool):
            return payload
        raise McpToolError("validate_meal_template_dishes", f"Expected bool, got {type(payload)!r}")

    async def get_dish(self, dish_id: str) -> DishDto:
        payload = await self._gateway.meal.call_tool("get_dish", {"dishId": dish_id})
        return self._parse_model(DishDto, payload, "get_dish")

    async def list_dishes(self) -> list[DishDto]:
        payload = await self._gateway.meal.call_tool("list_dishes")
        return self._parse_model_list(DishDto, payload, "list_dishes")

    async def list_dishes_by_category(self, category: str) -> list[DishDto]:
        payload = await self._gateway.meal.call_tool(
            "list_dishes_by_category",
            {"category": category},
        )
        return self._parse_model_list(DishDto, payload, "list_dishes_by_category")

    async def list_dishes_by_dietary_tag(self, dietary_tag: str) -> list[DishDto]:
        payload = await self._gateway.meal.call_tool(
            "list_dishes_by_dietary_tag",
            {"dietaryTag": dietary_tag},
        )
        return self._parse_model_list(DishDto, payload, "list_dishes_by_dietary_tag")

    async def list_dishes_by_popularity(self, min_popularity_score: float) -> list[DishDto]:
        payload = await self._gateway.meal.call_tool(
            "list_dishes_by_popularity",
            {"minPopularityScore": min_popularity_score},
        )
        return self._parse_model_list(DishDto, payload, "list_dishes_by_popularity")

    async def list_dishes_by_ingredient_id(self, ingredient_id: str) -> list[DishDto]:
        payload = await self._gateway.meal.call_tool(
            "list_dishes_by_ingredient_id",
            {"ingredientId": ingredient_id},
        )
        return self._parse_model_list(DishDto, payload, "list_dishes_by_ingredient_id")

    async def list_dishes_by_ingredient_name(self, ingredient_name: str) -> list[DishDto]:
        payload = await self._gateway.meal.call_tool(
            "list_dishes_by_ingredient_name",
            {"ingredientName": ingredient_name},
        )
        return self._parse_model_list(DishDto, payload, "list_dishes_by_ingredient_name")

    async def get_ingredient(self, ingredient_id: str) -> IngredientDto:
        payload = await self._gateway.meal.call_tool(
            "get_ingredient",
            {"ingredientId": ingredient_id},
        )
        return self._parse_model(IngredientDto, payload, "get_ingredient")

    async def get_ingredient_by_name(self, name: str) -> IngredientDto:
        payload = await self._gateway.meal.call_tool(
            "get_ingredient_by_name",
            {"name": name},
        )
        return self._parse_model(IngredientDto, payload, "get_ingredient_by_name")

    async def list_ingredients(self) -> list[IngredientDto]:
        payload = await self._gateway.meal.call_tool("list_ingredients")
        return self._parse_model_list(IngredientDto, payload, "list_ingredients")

    async def list_ingredients_by_category(self, category: str) -> list[IngredientDto]:
        payload = await self._gateway.meal.call_tool(
            "list_ingredients_by_category",
            {"category": category},
        )
        return self._parse_model_list(IngredientDto, payload, "list_ingredients_by_category")

    # --- customer-service tools ---

    async def get_subscription(self, subscription_id: str) -> UserSubscriptionDto:
        payload = await self._gateway.customer.call_tool(
            "get_subscription",
            {"subscriptionId": subscription_id},
        )
        return self._parse_model(UserSubscriptionDto, payload, "get_subscription")

    async def list_subscriptions_by_user_id(self, user_id: str) -> list[UserSubscriptionDto]:
        payload = await self._gateway.customer.call_tool(
            "list_subscriptions_by_user_id",
            {"userId": user_id},
        )
        return self._parse_model_list(UserSubscriptionDto, payload, "list_subscriptions_by_user_id")

    async def list_active_subscriptions_by_user_id(self, user_id: str) -> list[UserSubscriptionDto]:
        payload = await self._gateway.customer.call_tool(
            "list_active_subscriptions_by_user_id",
            {"userId": user_id},
        )
        return self._parse_model_list(
            UserSubscriptionDto,
            payload,
            "list_active_subscriptions_by_user_id",
        )

    async def list_subscriptions_by_plan_code(self, plan_code: str) -> list[UserSubscriptionDto]:
        payload = await self._gateway.customer.call_tool(
            "list_subscriptions_by_plan_code",
            {"planCode": plan_code},
        )
        return self._parse_model_list(
            UserSubscriptionDto,
            payload,
            "list_subscriptions_by_plan_code",
        )

    async def get_user_profile(self, user_id: str) -> UserProfileDto:
        payload = await self._gateway.customer.call_tool(
            "get_user_profile",
            {"userId": user_id},
        )
        return self._parse_model(UserProfileDto, payload, "get_user_profile")

    # --- composed workflows ---

    async def resolve_subscription_context(self, user_id: str) -> MealPlanSubscriptionContext:
        subscriptions = await self.list_active_subscriptions_by_user_id(user_id)
        if not subscriptions:
            raise McpToolError(
                "resolve_subscription_context",
                f"No active subscription found for user '{user_id}'",
            )

        subscription = subscriptions[0]
        plan_type = await self.get_meal_plan_type(subscription.plan_code)
        template = await self.get_meal_template(plan_type.template_id)

        return MealPlanSubscriptionContext(
            subscription=subscription,
            plan_type=plan_type,
            template=template,
        )

    @staticmethod
    def _parse_model(model: type[TModel], payload: Any, tool_name: str) -> TModel:
        if payload is None:
            raise McpToolError(tool_name, "Tool returned no payload")
        if not isinstance(payload, dict):
            raise McpToolError(tool_name, f"Expected object payload, got {type(payload)!r}")
        return model.model_validate(payload)

    @staticmethod
    def _parse_model_list(model: type[TModel], payload: Any, tool_name: str) -> list[TModel]:
        if payload is None:
            return []
        if not isinstance(payload, list):
            raise McpToolError(tool_name, f"Expected list payload, got {type(payload)!r}")
        return [model.model_validate(item) for item in payload]
