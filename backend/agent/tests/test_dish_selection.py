from foodopia_agent.schemas.customer import DietPreferenceDto, UserProfileDto
from foodopia_agent.schemas.meal import DishDto, DishIngredientDto, IngredientDto, MealTemplateDto, NutritionFactsDto
from foodopia_agent.services.dish_selection import (
    effective_ingredients_to_avoid,
    filter_candidates,
    select_meal_for_template,
)


def _ingredient(name: str) -> DishIngredientDto:
    return DishIngredientDto(
        ingredient_id=f"ing-{name}",
        ingredient=IngredientDto(name=name, unit_price=1.0, category="protein"),
        quantity=1.0,
    )


def _dish(
    dish_id: str,
    name: str,
    category: str,
    *,
    tags: list[str] | None = None,
    allergens: list[str] | None = None,
    ingredients: list[DishIngredientDto] | None = None,
    popularity: float = 1.0,
) -> DishDto:
    return DishDto(
        id=dish_id,
        name=name,
        category=category,
        serving_size=1,
        is_available=True,
        dietary_tags=tags or [],
        allergens=allergens or [],
        ingredients=ingredients or [],
        popularity_score=popularity,
        nutrition_per_serving=NutritionFactsDto(calories_kcal=400, protein_g=25),
    )


def test_filter_candidates_respects_template_and_allergies():
    template = MealTemplateDto(
        name="Low Fat",
        total_dishes=2,
        dish_categories={"main": 1, "side": 1},
        required_tags=["low-fat"],
        forbidden_tags=["pork-belly"],
    )
    profile = UserProfileDto(
        user_id="u1",
        diet_preference=DietPreferenceDto(allergies=["peanut"]),
    )
    dishes = [
        _dish("1", "Salmon", "main", tags=["low-fat"]),
        _dish("2", "Pork", "main", tags=["pork-belly"]),
        _dish("3", "Salad", "side", tags=["low-fat"], allergens=["peanut"]),
        _dish("4", "Broccoli", "side", tags=["low-fat"]),
    ]

    result = filter_candidates(dishes, template, profile)
    names = {dish.name for dish in result}

    assert names == {"Salmon", "Broccoli"}


def test_filter_candidates_merges_request_ingredients_to_avoid_with_profile():
    template = MealTemplateDto(
        name="Balanced",
        total_dishes=1,
        dish_categories={"main": 1},
    )
    profile = UserProfileDto(
        user_id="u1",
        diet_preference=DietPreferenceDto(foods_to_avoid=["onion"]),
    )
    dishes = [
        _dish("1", "Onion Soup", "main", ingredients=[_ingredient("onion")]),
        _dish("2", "Garlic Chicken", "main", ingredients=[_ingredient("garlic")]),
    ]

    result = filter_candidates(
        dishes,
        template,
        profile,
        ingredients_to_avoid=["garlic"],
    )
    assert [dish.name for dish in result] == []


def test_select_meal_swaps_to_cover_required_ingredients():
    template = MealTemplateDto(
        name="Balanced",
        total_dishes=2,
        dish_categories={"main": 1, "side": 1},
    )
    candidates = [
        _dish("1", "Chicken", "main", popularity=5.0, ingredients=[_ingredient("chicken")]),
        _dish("2", "Tofu", "main", popularity=4.0, ingredients=[_ingredient("tofu")]),
        _dish("3", "Rice", "side", popularity=5.0, ingredients=[_ingredient("rice")]),
        _dish("4", "Salmon Bowl", "side", popularity=3.0, ingredients=[_ingredient("salmon")]),
    ]

    selected = select_meal_for_template(candidates, template, ingredients_required=["salmon"])
    names = {dish.name for dish in selected}

    assert names == {"Chicken", "Salmon Bowl"}


def test_select_meal_raises_when_required_ingredients_cannot_be_satisfied():
    template = MealTemplateDto(
        name="Balanced",
        total_dishes=1,
        dish_categories={"main": 1},
    )
    candidates = [
        _dish("1", "Chicken", "main", ingredients=[_ingredient("chicken")]),
    ]

    try:
        select_meal_for_template(candidates, template, ingredients_required=["salmon"])
        raise AssertionError("Expected ValueError")
    except ValueError as exc:
        assert "salmon" in str(exc)


def test_effective_ingredients_to_avoid_merges_profile_and_request():
    profile = UserProfileDto(
        user_id="u1",
        diet_preference=DietPreferenceDto(allergies=["peanut"], foods_to_avoid=["shellfish"]),
    )

    blocked = effective_ingredients_to_avoid(profile, ["garlic"])

    assert blocked == {"peanut", "shellfish", "garlic"}
