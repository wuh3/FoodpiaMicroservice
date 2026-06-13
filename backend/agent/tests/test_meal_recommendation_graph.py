import pytest
from pydantic import SecretStr

from foodopia_agent.config import Settings
from foodopia_agent.graphs.meal_recommendation_graph import run_meal_recommendation
from foodopia_agent.main import create_app
from foodopia_agent.schemas.recommendation import MealPreferenceOverrides, MealRecommendationRequest
from foodopia_agent.tools.mcp_tools import FoodopiaMcpTools


class FakeServer:
    def __init__(self, responses: dict[str, object]):
        self.responses = responses

    async def call_tool(self, name: str, arguments: dict | None = None):
        return self.responses[name]


class FakeGateway:
    def __init__(self, meal_responses: dict[str, object], customer_responses: dict[str, object]):
        self.meal = FakeServer(meal_responses)
        self.customer = FakeServer(customer_responses)


@pytest.fixture
def fake_tools() -> FoodopiaMcpTools:
    gateway = FakeGateway(
        meal_responses={
            "get_meal_plan_type": {
                "planCode": "low_fat_plan",
                "displayName": "Low Fat Plan",
                "templateId": "tpl-1",
                "levels": [{"level": 1, "mealsPerMonth": 30, "monthlyPrice": 299}],
            },
            "get_meal_template": {
                "id": "tpl-1",
                "name": "Low Fat Template",
                "totalDishes": 2,
                "dishCategories": {"main": 1, "side": 1},
                "requiredTags": ["low-fat"],
                "forbiddenTags": [],
            },
            "list_dishes": [
                {
                    "id": "d1",
                    "name": "Grilled Fish",
                    "category": "main",
                    "servingSize": 1,
                    "isAvailable": True,
                    "dietaryTags": ["low-fat"],
                    "popularityScore": 5.0,
                    "ingredients": [
                        {
                            "ingredientId": "ing-fish",
                            "ingredient": {"name": "salmon", "unitPrice": 1.0, "category": "protein"},
                            "quantity": 1.0,
                        }
                    ],
                    "nutritionPerServing": {"caloriesKcal": 350, "proteinG": 30},
                },
                {
                    "id": "d2",
                    "name": "Steamed Veg",
                    "category": "side",
                    "servingSize": 1,
                    "isAvailable": True,
                    "dietaryTags": ["low-fat"],
                    "popularityScore": 4.0,
                    "ingredients": [
                        {
                            "ingredientId": "ing-broccoli",
                            "ingredient": {"name": "broccoli", "unitPrice": 1.0, "category": "vegetable"},
                            "quantity": 1.0,
                        }
                    ],
                    "nutritionPerServing": {"caloriesKcal": 80, "proteinG": 4},
                },
                {
                    "id": "d3",
                    "name": "Garlic Noodles",
                    "category": "side",
                    "servingSize": 1,
                    "isAvailable": True,
                    "dietaryTags": ["low-fat"],
                    "popularityScore": 5.0,
                    "ingredients": [
                        {
                            "ingredientId": "ing-garlic",
                            "ingredient": {"name": "garlic", "unitPrice": 1.0, "category": "vegetable"},
                            "quantity": 1.0,
                        }
                    ],
                    "nutritionPerServing": {"caloriesKcal": 200, "proteinG": 6},
                },
            ],
            "validate_meal_template_dishes": True,
        },
        customer_responses={
            "list_active_subscriptions_by_user_id": [
                {
                    "userId": "user-1",
                    "planCode": "low_fat_plan",
                    "planLevel": 1,
                    "mealsPerMonth": 30,
                    "startDate": "2026-01-01",
                }
            ],
            "get_user_profile": {
                "userId": "user-1",
                "dietPreference": {"allergies": [], "dietaryGoals": ["low-fat"]},
            },
        },
    )
    return FoodopiaMcpTools(gateway)


@pytest.mark.asyncio
async def test_meal_recommendation_graph_blocks_guarded_prompt(fake_tools, guard_agent):
    from foodopia_agent.exceptions.guard import GuardRejectedError

    with pytest.raises(GuardRejectedError):
        await run_meal_recommendation(
            fake_tools,
            MealRecommendationRequest(
                user_id="user-1",
                prompt="Which meal plan subscription should I choose?",
            ),
            guard_agent=guard_agent,
        )


@pytest.mark.asyncio
async def test_meal_recommendation_graph_avoids_request_ingredients(fake_tools, guard_agent):
    result = await run_meal_recommendation(
        fake_tools,
        MealRecommendationRequest(
            user_id="user-1",
            meal_preferences=MealPreferenceOverrides(ingredients_to_avoid=["garlic"]),
        ),
        guard_agent=guard_agent,
    )

    assert result.is_valid is True
    assert {dish.name for dish in result.dishes} == {"Grilled Fish", "Steamed Veg"}


@pytest.mark.asyncio
async def test_meal_recommendation_graph_happy_path(fake_tools, guard_agent):
    result = await run_meal_recommendation(
        fake_tools,
        MealRecommendationRequest(user_id="user-1"),
        guard_agent=guard_agent,
    )

    assert result.is_valid is True
    assert len(result.dishes) == 2
    assert result.plan_code == "low_fat_plan"
    assert "Grilled Fish" in result.rationale


def test_recommendation_endpoint_returns_503_without_mcp():
    app = create_app(
        Settings(openai_api_key=SecretStr("test-key")),
        connect_mcp=False,
    )
    from fastapi.testclient import TestClient

    client = TestClient(app)
    response = client.post(
        "/api/agent/recommendations/meals",
        json={"userId": "user-1"},
    )
    assert response.status_code == 503
