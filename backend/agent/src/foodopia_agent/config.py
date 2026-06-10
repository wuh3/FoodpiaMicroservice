"""Application settings loaded from environment / .env."""

from functools import lru_cache

from pydantic import Field, SecretStr
from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
        extra="ignore",
        case_sensitive=False,
    )

    app_name: str = Field(default="foodopia-agent-service", validation_alias="APP_NAME")
    debug: bool = Field(default=False, validation_alias="DEBUG")
    host: str = Field(default="0.0.0.0", validation_alias="HOST")
    port: int = Field(default=8090, validation_alias="PORT")

    openai_api_key: SecretStr = Field(..., validation_alias="OPENAI_API_KEY")
    openai_model: str = Field(default="gpt-4o-mini", validation_alias="OPENAI_MODEL")

    meal_mcp_url: str = Field(
        default="http://localhost:8082/mcp",
        validation_alias="MEAL_MCP_URL",
    )
    customer_mcp_url: str = Field(
        default="http://localhost:8083/mcp",
        validation_alias="CUSTOMER_MCP_URL",
    )

    meal_service_url: str = Field(
        default="http://localhost:8082",
        validation_alias="MEAL_SERVICE_URL",
    )
    customer_service_url: str = Field(
        default="http://localhost:8083",
        validation_alias="CUSTOMER_SERVICE_URL",
    )


@lru_cache
def get_settings() -> Settings:
    return Settings()
