# Foodopia Agent Service

Python agentic layer for meal recommendations. Communicates with `meal-service` and `customer-service` via MCP tools.

## Setup

```bash
cd backend/agent
cp .env.example .env   # set OPENAI_API_KEY
pip install -e ".[dev]"
```

## Run

```bash
foodopia-agent
# or
uvicorn foodopia_agent.main:app --reload --port 8090
```

## Health

- `GET /health` — liveness
- `GET /health/ready` — readiness (reports configured MCP URLs)
- `GET /docs` — OpenAPI UI
