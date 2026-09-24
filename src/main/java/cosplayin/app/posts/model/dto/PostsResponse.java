package cosplayin.app.posts.model.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import cosplayin.app.posts.model.type.PostsStatus;
import cosplayin.app.profiles.model.dto.DetailProfileDTO;
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
public class PostsResponse {
    private UUID postsId;
    private DetailProfileDTO author;
    private String caption;
    private PostsStatus status;
    private Long likeCount;
    private Long bookmarkCount;
    private Long shareCount;
    private LocalDateTime createdAt;
    private List<PostsMediaResponse> media;

}
