package cosplayin.app.posts.model.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import cosplayin.app.posts.model.type.PostsCommentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreatePostsDTO {
    private String caption;
    private PostsCommentStatus commentStatus;

    @NotNull
    private List<MultipartFile> media;
}
