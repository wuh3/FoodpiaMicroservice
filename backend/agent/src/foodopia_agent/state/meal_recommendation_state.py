"""Step 2 — LangGraph state: the shared notebook passed between nodes.

Each node reads the state, does one job, and returns a *partial update* (a dict
with only the keys it changed). LangGraph merges those updates into the full state.

We store DTOs as plain dicts so the state stays JSON-serializable for debugging
and future checkpointing.
"""

from __future__ import annotations

from typing import TypedDict


class MealRecommendationState(TypedDict, total=False):
    # --- input (set before graph runs) ---
    user_id: str
    prompt: str | None
    max_calories_kcal: float | None
    min_protein_g: float | None
    ingredients_to_avoid: list[str]
    ingredients_required: list[str]

    # --- guard ---
    guard_result: dict | None
    guard_blocked: bool

    # --- filled by nodes ---
    user_profile: dict | None
    subscription_context: dict | None
    candidate_dishes: list[dict]
    selected_dishes: list[dict]
    is_valid: bool
    rationale: str
    error: str | None
