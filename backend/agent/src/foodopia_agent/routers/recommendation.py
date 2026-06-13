"""Step 5 — HTTP router: exposes the graph as a REST endpoint."""

from __future__ import annotations

from fastapi import APIRouter, HTTPException, Request

from foodopia_agent.exceptions.guard import GuardRejectedError
from foodopia_agent.exceptions.mcp import McpToolError
from foodopia_agent.graphs.meal_recommendation_graph import run_meal_recommendation
from foodopia_agent.schemas.recommendation import MealRecommendationRequest, RecommendedMealSummary
from foodopia_agent.tools.mcp_tools import FoodopiaMcpTools

router = APIRouter(prefix="/api/agent", tags=["recommendations"])


def _get_mcp_tools(request: Request) -> FoodopiaMcpTools:
    tools = getattr(request.app.state, "mcp_tools", None)
    if tools is None:
        raise HTTPException(
            status_code=503,
            detail="MCP tools are unavailable. Ensure meal-service and customer-service are running.",
        )
    return tools


@router.post(
    "/recommendations/meals",
    response_model=RecommendedMealSummary,
    summary="Recommend a meal for a subscribed user",
)
async def recommend_meal(
    body: MealRecommendationRequest,
    request: Request,
) -> RecommendedMealSummary:
    tools = _get_mcp_tools(request)
    try:
        return await run_meal_recommendation(tools, body)
    except GuardRejectedError as exc:
        raise HTTPException(status_code=400, detail=exc.result.model_dump(by_alias=True)) from exc
    except McpToolError as exc:
        raise HTTPException(status_code=502, detail=str(exc)) from exc
