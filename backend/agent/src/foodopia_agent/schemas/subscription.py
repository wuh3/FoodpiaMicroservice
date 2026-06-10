"""Composed schemas for Use Case 9 — meal plan type & subscription flows."""

from __future__ import annotations

from typing import Optional

from pydantic import Field

from foodopia_agent.schemas.common import JavaDto
from foodopia_agent.schemas.customer import UserSubscriptionDto
from foodopia_agent.schemas.meal import MealPlanTypeDto, MealTemplateDto, PlanLevelDto


class MealPlanSubscriptionContext(JavaDto):
    """Resolved subscription context joining customer + meal MCP data.

    Built by the agent after:
      1. list_active_subscriptions_by_user_id / get_subscription
      2. get_meal_plan_type(plan_code)
      3. get_meal_template(template_id)
    """

    subscription: UserSubscriptionDto
    plan_type: MealPlanTypeDto
    template: MealTemplateDto

    @property
    def subscribed_level(self) -> Optional[PlanLevelDto]:
        return next(
            (level for level in self.plan_type.levels if level.level == self.subscription.plan_level),
            None,
        )
