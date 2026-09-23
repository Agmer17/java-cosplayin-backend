package cosplayin.app.core.event;

import java.util.UUID;

public record UsersDeletedEvent(
        UUID userId) {
}