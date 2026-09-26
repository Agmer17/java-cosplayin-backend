package cosplayin.app.likes.model.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record DetailLikesResponseDto(
        UUID likesId,
        UUID postsId,
        LikesAuthorDTO author,
        LocalDateTime createdAt) {

}
