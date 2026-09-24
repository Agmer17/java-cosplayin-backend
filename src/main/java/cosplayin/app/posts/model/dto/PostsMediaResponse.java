package cosplayin.app.posts.model.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import cosplayin.app.utils.storage.type.SupportedFileType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PostsMediaResponse {

    private UUID mediaId;
    private UUID postsId;
    private String mediaUrl;
    private SupportedFileType mediaType;
    private Short displayOrder;
    private LocalDateTime createdAt;

}
