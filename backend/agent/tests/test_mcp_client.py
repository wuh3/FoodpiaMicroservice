from mcp import types

from foodopia_agent.clients.mcp import parse_tool_result
from foodopia_agent.exceptions.mcp import McpToolError


def test_parse_tool_result_structured_content():
    result = types.CallToolResult(
        content=[],
        structuredContent={"planCode": "low_fat_slim_body_plan", "displayName": "Low Fat"},
    )
    payload = parse_tool_result(result)
    assert payload["planCode"] == "low_fat_slim_body_plan"


def test_parse_tool_result_json_text():
    result = types.CallToolResult(
        content=[types.TextContent(type="text", text='{"active": true}')],
    )
    payload = parse_tool_result(result)
    assert payload == {"active": True}


def test_parse_tool_result_boolean_text():
    result = types.CallToolResult(
        content=[types.TextContent(type="text", text="true")],
    )
    assert parse_tool_result(result) is True


def test_parse_tool_result_error():
    result = types.CallToolResult(
        content=[types.TextContent(type="text", text="Not found")],
        isError=True,
    )
    try:
        parse_tool_result(result)
        raise AssertionError("Expected McpToolError")
    except McpToolError as exc:
        assert "Not found" in str(exc)
