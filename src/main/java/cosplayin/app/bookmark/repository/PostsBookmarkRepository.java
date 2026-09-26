package cosplayin.app.bookmark.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cosplayin.app.bookmark.model.entity.PostsBookmark;

public interface PostsBookmarkRepository extends JpaRepository<PostsBookmark, UUID> {

    @Query("""
            SELECT b.posts.id
            FROM PostsBookmark b
            WHERE b.user.id = :id
            """)
    List<UUID> getBookmarkFromUsers(@Param("id") UUID id);

    Optional<PostsBookmark> findByPosts_IdAndUser_Id(UUID postsId, UUID userId);
}
