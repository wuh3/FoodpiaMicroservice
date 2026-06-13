"""FastAPI entry point for the Foodopia agent service."""

from __future__ import annotations

import logging
from contextlib import asynccontextmanager
from typing import Any, Optional

import uvicorn
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from foodopia_agent.clients.mcp import FoodopiaMcpGateway
from foodopia_agent.config import Settings, get_settings
from foodopia_agent.exceptions.mcp import McpConnectionError
from foodopia_agent.routers.recommendation import router as recommendation_router
from foodopia_agent.tools.mcp_tools import FoodopiaMcpTools

logger = logging.getLogger(__name__)


@asynccontextmanager
async def lifespan(app: FastAPI):
    settings: Settings = app.state.settings
    gateway: FoodopiaMcpGateway | None = None

    if settings.mcp_connect_on_startup:
        gateway = FoodopiaMcpGateway(
            meal_mcp_url=settings.meal_mcp_url,
            customer_mcp_url=settings.customer_mcp_url,
            timeout_seconds=settings.mcp_timeout_seconds,
        )
        try:
            await gateway.connect()
            app.state.mcp = gateway
            app.state.mcp_tools = FoodopiaMcpTools(gateway)
            logger.info("MCP gateway connected")
        except McpConnectionError as exc:
            logger.warning("MCP gateway unavailable at startup: %s", exc)
            app.state.mcp = None
            app.state.mcp_tools = None
    else:
        app.state.mcp = None
        app.state.mcp_tools = None

    try:
        yield
    finally:
        if gateway is not None:
            await gateway.close()


def create_app(
    settings: Optional[Settings] = None,
    *,
    connect_mcp: bool | None = None,
) -> FastAPI:
    settings = settings or get_settings()
    if connect_mcp is not None:
        settings = settings.model_copy(update={"mcp_connect_on_startup": connect_mcp})

    app = FastAPI(
        title=settings.app_name,
        description="Agentic layer for Foodopia meal recommendations and planning",
        version="0.1.0",
        lifespan=lifespan,
        debug=settings.debug,
    )
    app.state.settings = settings

    app.add_middleware(
        CORSMiddleware,
        allow_origins=["*"],
        allow_credentials=True,
        allow_methods=["*"],
        allow_headers=["*"],
    )

    @app.get("/health", tags=["health"])
    async def health() -> dict[str, Any]:
        return {
            "status": "UP",
            "service": settings.app_name,
        }

    @app.get("/health/ready", tags=["health"])
    async def readiness() -> dict[str, Any]:
        mcp_connected = getattr(app.state, "mcp", None) is not None
        return {
            "status": "READY" if mcp_connected or not settings.mcp_connect_on_startup else "DEGRADED",
            "service": settings.app_name,
            "dependencies": {
                "mealMcp": settings.meal_mcp_url,
                "customerMcp": settings.customer_mcp_url,
                "mcpConnected": mcp_connected,
            },
        }

    @app.get("/", tags=["health"])
    async def root() -> dict[str, str]:
        return {
            "service": settings.app_name,
            "docs": "/docs",
        }

    app.include_router(recommendation_router)

    return app


app = create_app()


def run() -> None:
    settings = get_settings()
    uvicorn.run(
        "foodopia_agent.main:app",
        host=settings.host,
        port=settings.port,
        reload=settings.debug,
    )


if __name__ == "__main__":
    run()
