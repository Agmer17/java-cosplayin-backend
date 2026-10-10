package cosplayin.app.likes.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cosplayin.app.likes.model.entity.CommentLikes;

public interface CommentLikesRepository extends JpaRepository<CommentLikes, UUID> {

    @Modifying
    @Query("""
            DELETE FROM CommentLikes cl
            WHERE cl.user.id = :userId
                AND cl.comment.id = :commentId
            """)
    int deleteByCommentIdAndUserId(
            @Param("userId") UUID userId,
            @Param("commentId") UUID commentId);

}
