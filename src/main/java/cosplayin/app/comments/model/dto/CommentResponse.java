package cosplayin.app.comments.model.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record CommentResponse(
        UUID id,
        UUID postsId,
        String comment,
        String profilePicture,
        Integer replyCount,
        String username,
        UUID parentId,
        Integer likeCount,
        LocalDateTime createdAt) {

}
