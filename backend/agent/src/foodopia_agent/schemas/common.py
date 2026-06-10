"""Shared enums and response wrappers mirroring Java DTOs."""

from __future__ import annotations

from datetime import datetime
from enum import Enum
from typing import Union

from pydantic import BaseModel, ConfigDict, Field
from pydantic.alias_generators import to_camel


class JavaDto(BaseModel):
    """Base model for deserializing JSON from Spring Boot services (camelCase)."""

    model_config = ConfigDict(
        populate_by_name=True,
        alias_generator=to_camel,
    )


class SubscriptionStatus(str, Enum):
    ACTIVE = "ACTIVE"
    PAUSED = "PAUSED"
    CANCELLED = "CANCELLED"


class SavoryIntensity(str, Enum):
    LIGHT = "LIGHT"
    MEDIUM = "MEDIUM"
    STRONG = "STRONG"


class ResponseDto(JavaDto):
    status_code: str
    status_msg: str


class ErrorResponseDto(JavaDto):
    api_path: str
    error_code: Union[str, int] = Field(
        description="HttpStatus enum name (e.g. NOT_FOUND) or numeric code from Spring"
    )
    error_message: str
    error_time: datetime
