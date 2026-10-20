package cosplayin.app.notification.model.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PostsPreviewNotificationDTO(
        UUID postId,
        String thumbnail
) {
}
