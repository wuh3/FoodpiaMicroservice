from fastapi.testclient import TestClient

from pydantic import SecretStr

from foodopia_agent.config import Settings
from foodopia_agent.main import create_app


def test_health():
    app = create_app(
        Settings(openai_api_key=SecretStr("test-key"))
    )
    client = TestClient(app)

    response = client.get("/health")
    assert response.status_code == 200
    body = response.json()
    assert body["status"] == "UP"
    assert body["service"] == "foodopia-agent-service"
