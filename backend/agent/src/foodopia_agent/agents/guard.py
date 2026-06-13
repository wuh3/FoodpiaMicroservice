"""Guard agent: LLM-based in-scope, intent, and safety checks."""

from __future__ import annotations

from collections.abc import Awaitable, Callable

from langchain_core.messages import HumanMessage, SystemMessage
from langchain_openai import ChatOpenAI

from foodopia_agent.config import Settings
from foodopia_agent.schemas.guard import GuardResult, RecommendationIntent

LlmEvaluator = Callable[[str, RecommendationIntent], Awaitable[GuardResult]]

GUARD_SYSTEM_PROMPT = """You are the guard agent for Foodopia, a meal subscription platform.

Your job is to review a user prompt before the meal recommendation workflow runs.

Classify the prompt and decide whether it may proceed to the meal recommendation endpoint.

Set fields as follows:
- in_scope: true only if the prompt is about Foodopia meals, food choices, nutrition, or diet.
- intent: one of meal_recommendation, subscription_plan, out_of_scope
- safety_flags: short labels for harmful, abusive, illegal, or self-harm requests; otherwise []
- reason: one concise sentence explaining approval or rejection, maximum 50 words

Rules:
- meal_recommendation: user wants help choosing what to eat for a meal now.
- subscription_plan: user wants help choosing or comparing meal plans/subscription tiers.
- out_of_scope: unrelated topics such as finance, weather, coding, politics, or general chat.
- If the prompt is unsafe, set in_scope=false, add safety_flags, and explain briefly in reason.
- For approved meal requests, reason may briefly confirm why it is allowed.
"""


class GuardAgent:
    """Classifies user prompts before routing to a recommendation workflow."""

    def __init__(
        self,
        *,
        openai_api_key: str,
        openai_model: str = "gpt-4o-mini",
        llm_evaluator: LlmEvaluator | None = None,
    ):
        self.openai_api_key = openai_api_key
        self.openai_model = openai_model
        self._llm_evaluator = llm_evaluator

    @classmethod
    def from_settings(cls, settings: Settings) -> GuardAgent:
        return cls(
            openai_api_key=settings.openai_api_key.get_secret_value(),
            openai_model=settings.openai_model,
        )

    async def evaluate(
        self,
        prompt: str | None,
        *,
        expected_intent: RecommendationIntent = RecommendationIntent.MEAL_RECOMMENDATION,
    ) -> GuardResult:
        text = (prompt or "").strip()

        if not text:
            return GuardResult(
                in_scope=True,
                intent=expected_intent,
                reason="Structured meal recommendation request accepted.",
            )

        result = await self._classify_prompt(text)

        if result.intent != expected_intent:
            return GuardResult(
                in_scope=result.in_scope,
                intent=result.intent,
                safety_flags=result.safety_flags,
                reason=result.reason
                or f"This endpoint only supports {expected_intent.value.replace('_', ' ')} requests.",
            )

        return result

    async def _classify_prompt(self, text: str) -> GuardResult:
        if self._llm_evaluator is not None:
            return await self._llm_evaluator(text, RecommendationIntent.MEAL_RECOMMENDATION)

        llm = ChatOpenAI(
            api_key=self.openai_api_key,
            model=self.openai_model,
            temperature=0,
        ).with_structured_output(GuardResult)

        result = await llm.ainvoke(
            [
                SystemMessage(content=GUARD_SYSTEM_PROMPT),
                HumanMessage(content=text),
            ]
        )

        if isinstance(result, GuardResult):
            return result
        return GuardResult.model_validate(result)
