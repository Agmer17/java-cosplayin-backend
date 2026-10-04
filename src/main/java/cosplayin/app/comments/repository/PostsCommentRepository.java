package cosplayin.app.comments.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cosplayin.app.comments.model.entity.PostsComment;
import cosplayin.app.comments.model.projection.DeleteCommentInfoProjection;

public interface PostsCommentRepository extends JpaRepository<PostsComment, UUID> {

    @Query("""
            select
                c.id as commentId,
                c.user.id as commentAuthorId,
                c.posts.author.id as postAuthorId
            """ +
            "from PostsComment c " +
            "where c.id = :commentId")
    Optional<DeleteCommentInfoProjection> findDeleteProjectionById(
            @Param("commentId") UUID commentId);
}
