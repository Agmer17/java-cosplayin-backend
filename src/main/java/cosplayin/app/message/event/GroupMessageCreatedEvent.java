package cosplayin.app.message.event;

import cosplayin.app.message.model.dto.MessageResponseDTO;

import java.util.List;
import java.util.UUID;

public record GroupMessageCreatedEvent(
        List<UUID> receiver,
        MessageResponseDTO messageResponseDTO
) {
}
