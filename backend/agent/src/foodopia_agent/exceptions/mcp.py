"""MCP client errors."""


class McpConnectionError(RuntimeError):
    """Raised when the agent cannot connect to an MCP server."""


class McpToolError(RuntimeError):
    """Raised when an MCP tool call fails or returns an unexpected payload."""

    def __init__(self, tool_name: str, message: str):
        self.tool_name = tool_name
        super().__init__(f"MCP tool '{tool_name}' failed: {message}")
