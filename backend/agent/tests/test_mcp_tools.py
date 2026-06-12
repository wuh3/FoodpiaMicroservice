import pytest

from foodopia_agent.exceptions.mcp import McpToolError
from foodopia_agent.tools.mcp_tools import FoodopiaMcpTools


class FakeServer:
    def __init__(self, responses: dict[str, object]):
        self.responses = responses
        self.calls: list[tuple[str, dict]] = []

    async def call_tool(self, name: str, arguments: dict | None = None):
        self.calls.append((name, arguments or {}))
        if name not in self.responses:
            raise AssertionError(f"Unexpected tool call: {name}")
        return self.responses[name]


class FakeGateway:
    def __init__(self, meal_responses: dict[str, object], customer_responses: dict[str, object]):
        self.meal = FakeServer(meal_responses)
        self.customer = FakeServer(customer_responses)


@pytest.mark.asyncio
async def test_resolve_subscription_context():
    gateway = FakeGateway(
        meal_responses={
            "get_meal_plan_type": {
                "planCode": "low_fat_slim_body_plan",
                "displayName": "Low Fat Slim Body Plan",
                "templateId": "tpl-1",
                "levels": [{"level": 2, "mealsPerMonth": 45, "monthlyPrice": 399.0}],
            },
            "get_meal_template": {
                "name": "Low Fat Template",
                "totalDishes": 3,
                "dishCategories": {"main": 1, "side": 2},
                "requiredTags": ["low-fat"],
                "forbiddenTags": ["pork-belly"],
            },
        },
        customer_responses={
            "list_active_subscriptions_by_user_id": [
                {
                    "userId": "user-1",
                    "planCode": "low_fat_slim_body_plan",
                    "planLevel": 2,
                    "mealsPerMonth": 45,
                    "startDate": "2026-01-01",
                }
            ],
        },
    )

    tools = FoodopiaMcpTools(gateway)
    context = await tools.resolve_subscription_context("user-1")

    assert context.subscription.plan_code == "low_fat_slim_body_plan"
    assert context.plan_type.template_id == "tpl-1"
    assert context.template.required_tags == ["low-fat"]
    assert context.subscribed_level is not None
    assert context.subscribed_level.meals_per_month == 45
    assert gateway.customer.calls[0][0] == "list_active_subscriptions_by_user_id"
    assert gateway.meal.calls[0][0] == "get_meal_plan_type"


@pytest.mark.asyncio
async def test_resolve_subscription_context_requires_active_subscription():
    gateway = FakeGateway(meal_responses={}, customer_responses={"list_active_subscriptions_by_user_id": []})
    tools = FoodopiaMcpTools(gateway)

    with pytest.raises(McpToolError, match="No active subscription"):
        await tools.resolve_subscription_context("user-404")
