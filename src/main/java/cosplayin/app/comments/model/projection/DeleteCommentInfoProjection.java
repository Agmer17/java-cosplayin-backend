package cosplayin.app.comments.model.projection;

import jakarta.annotation.Nullable;

import java.util.UUID;

public interface DeleteCommentInfoProjection {
    UUID getCommentId();

    UUID getCommentAuthorId();

    UUID getPostAuthorId();

    @Nullable
    UUID getParentComment();

    UUID getPostsId();
}
