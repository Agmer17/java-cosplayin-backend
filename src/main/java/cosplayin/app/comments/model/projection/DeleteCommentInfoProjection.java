package cosplayin.app.comments.model.projection;

import java.util.UUID;

public interface DeleteCommentInfoProjection {
    UUID getCommentId();

    UUID getCommentAuthorId();

    UUID getPostAuthorId();
}
