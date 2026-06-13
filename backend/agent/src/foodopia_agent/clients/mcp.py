"""MCP client wrappers for Foodopia meal and customer servers."""

from __future__ import annotations

import json
import logging
from contextlib import AsyncExitStack
from typing import Any

from mcp import ClientSession, types
from mcp.client.sse import sse_client

from foodopia_agent.exceptions.mcp import McpConnectionError, McpToolError

logger = logging.getLogger(__name__)


def parse_tool_result(result: types.CallToolResult) -> Any:
    """Extract JSON payload from an MCP tool result."""
    if result.isError:
        message = _format_error_content(result.content)
        raise McpToolError("unknown", message)

    if result.structuredContent is not None:
        return result.structuredContent

    if not result.content:
        return None

    text_parts: list[str] = []
    for block in result.content:
        if isinstance(block, types.TextContent):
            text_parts.append(block.text.strip())

    if not text_parts:
        return None

    combined = "\n".join(part for part in text_parts if part)
    if not combined:
        return None

    try:
        return json.loads(combined)
    except json.JSONDecodeError:
        lowered = combined.lower()
        if lowered in {"true", "false"}:
            return lowered == "true"
        return combined


def _format_error_content(content: list[types.ContentBlock]) -> str:
    messages: list[str] = []
    for block in content:
        if isinstance(block, types.TextContent):
            messages.append(block.text)
    return "; ".join(messages) if messages else "Unknown MCP tool error"


class McpServerClient:
    """Single-server MCP client using SSE transport (Spring AI 1.0 webmvc)."""

    def __init__(self, name: str, url: str, timeout_seconds: float = 30.0):
        self.name = name
        self.url = url
        self.timeout_seconds = timeout_seconds
        self._stack: AsyncExitStack | None = None
        self._session: ClientSession | None = None

    @property
    def session(self) -> ClientSession:
        if self._session is None:
            raise McpConnectionError(f"MCP server '{self.name}' is not connected")
        return self._session

    async def connect(self) -> None:
        if self._session is not None:
            return

        stack = AsyncExitStack()
        try:
            read_stream, write_stream = await stack.enter_async_context(
                sse_client(
                    self.url,
                    timeout=self.timeout_seconds,
                    sse_read_timeout=300.0,
                )
            )
            session = await stack.enter_async_context(ClientSession(read_stream, write_stream))
            await session.initialize()
        except Exception as exc:
            await stack.aclose()
            raise McpConnectionError(
                f"Failed to connect to MCP server '{self.name}' at {self.url}: {exc}"
            ) from exc

        self._stack = stack
        self._session = session
        logger.info("Connected to MCP server '%s' at %s", self.name, self.url)

    async def close(self) -> None:
        if self._stack is None:
            return

        await self._stack.aclose()
        self._stack = None
        self._session = None
        logger.info("Closed MCP connection to '%s'", self.name)

    async def call_tool(self, name: str, arguments: dict[str, Any] | None = None) -> Any:
        try:
            result = await self.session.call_tool(name, arguments or {})
        except Exception as exc:
            raise McpToolError(name, str(exc)) from exc

        if result.isError:
            raise McpToolError(name, _format_error_content(result.content))

        try:
            return parse_tool_result(result)
        except McpToolError:
            raise
        except Exception as exc:
            raise McpToolError(name, f"Unexpected tool response: {exc}") from exc

    async def list_tools(self) -> list[str]:
        tools = await self.session.list_tools()
        return [tool.name for tool in tools.tools]


class FoodopiaMcpGateway:
    """Gateway to meal-service and customer-service MCP servers."""

    def __init__(
        self,
        meal_mcp_url: str,
        customer_mcp_url: str,
        timeout_seconds: float = 30.0,
    ):
        self.meal = McpServerClient("meal", meal_mcp_url, timeout_seconds)
        self.customer = McpServerClient("customer", customer_mcp_url, timeout_seconds)

    async def connect(self) -> None:
        await self.meal.connect()
        await self.customer.connect()

    async def close(self) -> None:
        await self.customer.close()
        await self.meal.close()

    async def ping(self) -> dict[str, list[str]]:
        return {
            "meal": await self.meal.list_tools(),
            "customer": await self.customer.list_tools(),
        }
