package cosplayin.app.comments.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cosplayin.app.comments.model.entity.PostsComment;
import cosplayin.app.comments.model.projection.DeleteCommentInfoProjection;

public interface PostsCommentRepository extends JpaRepository<PostsComment, UUID> {

    @Query("""
            select
                c.id as commentId,
                c.user.id as commentAuthorId,
                c.posts.author.id as postAuthorId,
                c.parentComment.id as parentComment,
                c.posts.id as postsId
            """ +
            "from PostsComment c " +
            "where c.id = :commentId")
    Optional<DeleteCommentInfoProjection> findDeleteProjectionById(
            @Param("commentId") UUID commentId);

    @Modifying
    @Query("""
    UPDATE PostsComment c
    SET c.replyCount = c.replyCount - 1
    WHERE c.id = :parentCommentId
      AND c.replyCount > 0
    """)
    void decrementParentReplyCount(
            @Param("parentCommentId") UUID parentCommentId
    );

    @Query("""
          SELECT COUNT(pc) FROM PostsComment pc WHERE pc.parentComment.id = :id
          """)
    long countTheReplies(@Param("id") UUID id);
}
