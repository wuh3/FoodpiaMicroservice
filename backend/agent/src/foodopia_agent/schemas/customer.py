"""Customer-service MCP tool return types (customer-mcp-server)."""

from __future__ import annotations

from datetime import date, datetime
from typing import Optional

from pydantic import Field

from foodopia_agent.schemas.common import JavaDto, SavoryIntensity, SubscriptionStatus


class DietPreferenceDto(JavaDto):
    savory: Optional[SavoryIntensity] = None
    dietary_goals: list[str] = Field(default_factory=list)
    allergies: list[str] = Field(default_factory=list)
    foods_to_avoid: list[str] = Field(default_factory=list)


class UserProfileDto(JavaDto):
    id: Optional[str] = None
    user_id: str
    profile_pic_url: Optional[str] = None
    legal_name: Optional[str] = None
    nickname: Optional[str] = None
    phone: Optional[str] = None
    diet_preference: Optional[DietPreferenceDto] = None
    created_at: Optional[datetime] = None
    updated_at: Optional[datetime] = None


class UserSubscriptionDto(JavaDto):
    id: Optional[str] = None
    user_id: str
    plan_name: Optional[str] = None
    plan_code: str
    plan_level: int = Field(gt=0)
    meals_per_month: int = 0
    status: Optional[SubscriptionStatus] = None
    start_date: date
    end_date: Optional[date] = None
    created_at: Optional[datetime] = None
    updated_at: Optional[datetime] = None
