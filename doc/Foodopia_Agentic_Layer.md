# Agentic layer design doc

This is the agentic layer for the Foodopia microservice designated to provide users smart recommendations on Subscription Options, Dish Combinations, Delivery Scheduling, etc. This layer will be an individual service and communicate with other services via MCP tools and HTTP calls.

## Architecture

```
backend/agent/
├── src/foodopia_agent/
│   ├── __init__.py
│   ├── main.py                          # FastAPI app entry + lifespan
│   ├── config.py                        # Pydantic settings (LLM, MCP URLs, Eureka)
│   │
│   ├── routers/                         # FastAPI endpoints
│   │   ├── __init__.py
│   │   ├── recommendation.py            # POST /api/agent/recommendations
│   │   ├── combination.py               # POST /api/agent/combinations
│   │   └── scheduling.py                # POST /api/agent/scheduling
│   │
│   ├── graphs/                          # LangGraph workflows
│   │   ├── __init__.py
│   │   ├── recommendation_graph.py      # Sequential: profile → menu → nutrition → recommender
│   │   ├── combination_graph.py         # Cyclical: menu ⇄ nutrition (loop until valid)
│   │   ├── scheduling_graph.py          # Parallel branches → merge
│   │   └── builder.py                   # Shared graph compilation utilities
│   │
│   ├── agents/                          # Agent definitions (LangChain AgentExecutor / nodes)
│   │   ├── __init__.py
│   │   ├── profile.py                   # Reads user preferences/history
│   │   ├── nutrition.py                 # Validates against health goals
│   │   ├── menu.py                      # Queries dish catalog
│   │   └── supervisor.py                # Orchestrator/synthesizer
│   │
│   ├── state/                           # LangGraph state schemas (TypedDict / Pydantic)
│   │   ├── __init__.py
│   │   ├── recommendation_state.py
│   │   ├── combination_state.py
│   │   └── scheduling_state.py
│   │
│   ├── tools/                           # Tools exposed to agents
│   │   ├── __init__.py                  # Aggregates all tools for binding
│   │   ├── mcp_tools.py                 # Auto-loaded from meal + customer MCP servers
│   │   ├── operations.py                # HTTP tool (via Eureka)
│   │   ├── delivery.py                  # HTTP tool (via Eureka)
│   │   └── kitchen.py                   # HTTP tool (via Eureka)
│   │
│   ├── prompts/                         # System/user prompt templates per agent
│   │   ├── __init__.py
│   │   ├── profile.py
│   │   ├── nutrition.py
│   │   ├── menu.py
│   │   └── supervisor.py
│   │
│   ├── schemas/                         # Pydantic request/response models
│   │   ├── __init__.py
│   │   ├── recommendation.py            # RecommendationRequest, RecommendationResponse
│   │   ├── combination.py
│   │   ├── scheduling.py
│   │   └── common.py                    # ResponseDto, ErrorResponseDto (matches Java)
│   │
│   ├── clients/                         # External service clients
│   │   ├── __init__.py
│   │   ├── mcp.py                       # MultiServerMCPClient wrapper
│   │   ├── http.py                      # httpx client with retries/timeouts
│   │   ├── service_registry.py          # Eureka resolver
│   │   └── llm.py                       # Anthropic client factory
│   │
│   ├── persistence/                     # MongoDB + LangGraph checkpointing
│   │   ├── __init__.py
│   │   ├── mongo.py                     # Motor (async MongoDB) client
│   │   ├── checkpointer.py              # LangGraph MongoDB checkpoint saver
│   │   └── session_repository.py        # Agent session persistence
│   │
│   ├── exceptions/                      # Custom exceptions + FastAPI handlers
│   │   ├── __init__.py
│   │   └── handlers.py
│   │
│   └── constants.py                     # Status codes, messages (mirrors MealConstants style)
│
├── tests/
│   ├── unit/
│   │   ├── agents/
│   │   ├── graphs/
│   │   └── tools/
│   └── integration/
│       └── test_recommendation_flow.py
│
├── Dockerfile
├── pyproject.toml                       # Poetry/uv dependencies
├── README.md
└── .env.example
```

## Design Decisions

**Language:** Python

**Framework:** 

- LangGraph / CrewAI for agents
- MCP Python SDK for tool calls
- FastAPI for HTTP requests

**Inter-service:** Bypass Gateway since they are internal to interbal communications. However, need to extra handle the authentications without the Gateway. In addition, we chose to use **Distributed MCP Servers**  instead of centralized MCP server. Each individual service takes ownership of its own MCP server. Tools live next to the data they expose; schema drift is minimized. Independent deployment of tool changes. Scales horizontally as services grow. But, it is expected that only a few services require MCP servers such as meal, customer, scheduling.

**Service discovery:** Kubernetes

**Updated individual service architecture (with MCP)**

```
backend/
├── meal/
│   └── src/main/java/com/foodopia/meal/
│       ├── controller/         # existing REST
│       ├── service/            # existing business logic
│       └── mcp/                # NEW: MCP server
│           ├── McpConfig.java
│           └── tools/
│               ├── DishTools.java
│               └── IngredientTools.java
└── customer/
    └── src/main/java/com/foodopia/customer/
        └── mcp/                # NEW: MCP server
            ├── McpConfig.java
            └── tools/
                ├── UserTools.java
                └── PreferenceTools.java
```

