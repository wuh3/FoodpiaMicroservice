"""Guard agent errors."""

from foodopia_agent.schemas.guard import GuardResult


class GuardRejectedError(Exception):
    """Raised when the guard blocks a request before the recommendation pipeline runs."""

    def __init__(self, result: GuardResult):
        self.result = result
        super().__init__(result.reason or "Request blocked by guard agent")
