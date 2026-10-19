package cosplayin.app.posts.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import cosplayin.app.posts.model.entity.Posts;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostsRepository extends JpaRepository<Posts, UUID> {

    @Modifying
    @Query("""
    UPDATE Posts p
    SET p.commentCount = p.commentCount - :count
    WHERE p.id = :postsId
      AND p.commentCount > 0
    """)
    void decrementPostsCommentCount(
            @Param("postsId") UUID postsId,
            @Param("count") long count
    );

}
