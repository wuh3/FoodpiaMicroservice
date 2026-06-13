"""Pure selection logic
"""

from __future__ import annotations

from collections import defaultdict

from foodopia_agent.schemas.customer import UserProfileDto
from foodopia_agent.schemas.meal import DishDto, MealTemplateDto, NutritionFactsDto


def _normalize_names(names: list[str]) -> set[str]:
    return {name.strip().lower() for name in names if name and name.strip()}


def _normalize_tags(tags: list[str]) -> set[str]:
    return _normalize_names(tags)


def _ingredient_name_matches(ingredient_name: str, target: str) -> bool:
    left = ingredient_name.strip().lower()
    right = target.strip().lower()
    if not left or not right:
        return False
    return left == right or right in left or left in right


def _dish_ingredient_names(dish: DishDto) -> set[str]:
    names: set[str] = set()
    if not dish.ingredients:
        return names

    for entry in dish.ingredients:
        if entry.ingredient and entry.ingredient.name:
            names.add(entry.ingredient.name.strip().lower())
    return names


def _profile_ingredients_to_avoid(profile: UserProfileDto | None) -> set[str]:
    if profile is None or profile.diet_preference is None:
        return set()

    blocked = list(profile.diet_preference.allergies)
    blocked.extend(profile.diet_preference.foods_to_avoid)
    return _normalize_names(blocked)


def effective_ingredients_to_avoid(
    profile: UserProfileDto | None,
    request_ingredients_to_avoid: list[str] | None,
) -> set[str]:
    blocked = _profile_ingredients_to_avoid(profile)
    blocked.update(_normalize_names(request_ingredients_to_avoid or []))
    return blocked


def dish_matches_template(dish: DishDto, template: MealTemplateDto) -> bool:
    tags = _normalize_tags(dish.dietary_tags)
    required = _normalize_tags(template.required_tags)
    forbidden = _normalize_tags(template.forbidden_tags)

    if required and not required.issubset(tags):
        return False
    if tags & forbidden:
        return False
    return True


def dish_contains_avoided_ingredient(dish: DishDto, ingredients_to_avoid: set[str]) -> bool:
    if not ingredients_to_avoid:
        return False

    dish_allergens = _normalize_tags(dish.allergens)
    if ingredients_to_avoid & dish_allergens:
        return True

    for ingredient_name in _dish_ingredient_names(dish):
        for avoided in ingredients_to_avoid:
            if _ingredient_name_matches(ingredient_name, avoided):
                return True
    return False


def meal_covers_required_ingredients(
    dishes: list[DishDto],
    ingredients_required: set[str],
) -> bool:
    if not ingredients_required:
        return True

    covered: set[str] = set()
    for dish in dishes:
        dish_names = _dish_ingredient_names(dish)
        dish_allergens = _normalize_tags(dish.allergens)
        searchable = dish_names | dish_allergens

        for required in ingredients_required:
            if any(_ingredient_name_matches(name, required) for name in searchable):
                covered.add(required)

    return covered == ingredients_required


def missing_required_ingredients(
    dishes: list[DishDto],
    ingredients_required: set[str],
) -> set[str]:
    if not ingredients_required:
        return set()

    covered: set[str] = set()
    for dish in dishes:
        searchable = _dish_ingredient_names(dish) | _normalize_tags(dish.allergens)
        for required in ingredients_required:
            if any(_ingredient_name_matches(name, required) for name in searchable):
                covered.add(required)

    return ingredients_required - covered


def filter_candidates(
    dishes: list[DishDto],
    template: MealTemplateDto,
    profile: UserProfileDto | None,
    ingredients_to_avoid: list[str] | None = None,
) -> list[DishDto]:
    blocked = effective_ingredients_to_avoid(profile, ingredients_to_avoid)
    candidates: list[DishDto] = []

    for dish in dishes:
        if not dish.is_available:
            continue
        if not dish_matches_template(dish, template):
            continue
        if dish_contains_avoided_ingredient(dish, blocked):
            continue
        candidates.append(dish)

    return candidates


def filter_by_nutrition_targets(
    dishes: list[DishDto],
    template: MealTemplateDto,
    max_calories_kcal: float | None,
    min_protein_g: float | None,
) -> list[DishDto]:
    if max_calories_kcal is None and min_protein_g is None:
        return dishes

    per_dish_calorie_cap = (
        max_calories_kcal / template.total_dishes if max_calories_kcal is not None else None
    )
    filtered: list[DishDto] = []

    for dish in dishes:
        nutrition = dish.nutrition_per_serving
        if nutrition is None:
            filtered.append(dish)
            continue

        if per_dish_calorie_cap is not None and nutrition.calories_kcal > per_dish_calorie_cap:
            continue
        if min_protein_g is not None and nutrition.protein_g < (min_protein_g / template.total_dishes):
            continue
        filtered.append(dish)

    return filtered


def _greedy_pick_by_category(
    candidates: list[DishDto],
    template: MealTemplateDto,
) -> list[DishDto]:
    by_category: dict[str, list[DishDto]] = defaultdict(list)
    for dish in candidates:
        by_category[dish.category].append(dish)

    for category_dishes in by_category.values():
        category_dishes.sort(key=lambda dish: dish.popularity_score, reverse=True)

    selected: list[DishDto] = []
    for category, required_count in template.dish_categories.items():
        pool = by_category.get(category, [])
        if len(pool) < required_count:
            raise ValueError(
                f"Not enough candidate dishes for category '{category}' "
                f"(need {required_count}, have {len(pool)})"
            )
        selected.extend(pool[:required_count])

    if len(selected) != template.total_dishes:
        raise ValueError(
            f"Selection size {len(selected)} does not match template total "
            f"{template.total_dishes}"
        )

    return selected


def _try_satisfy_required_ingredients(
    selected: list[DishDto],
    candidates: list[DishDto],
    template: MealTemplateDto,
    ingredients_required: set[str],
) -> list[DishDto] | None:
    if meal_covers_required_ingredients(selected, ingredients_required):
        return selected

    by_category: dict[str, list[DishDto]] = defaultdict(list)
    for dish in candidates:
        by_category[dish.category].append(dish)

    current = list(selected)
    for category, required_count in template.dish_categories.items():
        pool = by_category.get(category, [])
        if not pool:
            continue

        category_indices = [index for index, dish in enumerate(current) if dish.category == category]
        if len(category_indices) != required_count:
            continue

        for slot_index in category_indices:
            for replacement in pool:
                if replacement.id == current[slot_index].id:
                    continue
                trial = list(current)
                trial[slot_index] = replacement
                if meal_covers_required_ingredients(trial, ingredients_required):
                    return trial

    return None


def select_meal_for_template(
    candidates: list[DishDto],
    template: MealTemplateDto,
    ingredients_required: list[str] | None = None,
) -> list[DishDto]:
    """Greedy pick per category, then swap within category to cover required ingredients."""
    required = _normalize_names(ingredients_required or [])
    selected = _greedy_pick_by_category(candidates, template)

    if not required:
        return selected

    if meal_covers_required_ingredients(selected, required):
        return selected

    adjusted = _try_satisfy_required_ingredients(selected, candidates, template, required)
    if adjusted is not None:
        return adjusted

    missing = ", ".join(sorted(missing_required_ingredients(selected, required)))
    raise ValueError(f"Cannot satisfy required ingredients: {missing}")


def compute_nutrition_totals(dishes: list[DishDto]) -> NutritionFactsDto:
    totals = NutritionFactsDto()
    for dish in dishes:
        nutrition = dish.nutrition_per_serving
        if nutrition is None:
            continue
        totals.calories_kcal += nutrition.calories_kcal
        totals.protein_g += nutrition.protein_g
        totals.sugar_g += nutrition.sugar_g
        totals.salt_mg += nutrition.salt_mg
    return totals
