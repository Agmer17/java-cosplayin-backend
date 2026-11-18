package cosplayin.app.message.event;


import cosplayin.app.message.model.dto.MessageResponseDTO;

import java.util.UUID;

public record PrivateMessageCreatedEvent(
        UUID sender,
        UUID target,
        MessageResponseDTO message
) {
}
