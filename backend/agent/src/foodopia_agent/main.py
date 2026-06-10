"""FastAPI entry point for the Foodopia agent service."""

from __future__ import annotations

from contextlib import asynccontextmanager
from typing import Any, Optional

import uvicorn
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from foodopia_agent.config import Settings, get_settings


@asynccontextmanager
async def lifespan(app: FastAPI):
    settings = get_settings()
    app.state.settings = settings
    yield


def create_app(settings: Optional[Settings] = None) -> FastAPI:
    settings = settings or get_settings()

    app = FastAPI(
        title=settings.app_name,
        description="Agentic layer for Foodopia meal recommendations and planning",
        version="0.1.0",
        lifespan=lifespan,
        debug=settings.debug,
    )

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
        return {
            "status": "READY",
            "service": settings.app_name,
            "dependencies": {
                "mealMcp": settings.meal_mcp_url,
                "customerMcp": settings.customer_mcp_url,
            },
        }

    @app.get("/", tags=["health"])
    async def root() -> dict[str, str]:
        return {
            "service": settings.app_name,
            "docs": "/docs",
        }

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
