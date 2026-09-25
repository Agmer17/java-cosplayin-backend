package cosplayin.app.posts.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cosplayin.app.posts.model.dto.PostsMediaResponse;
import cosplayin.app.posts.model.entity.Posts;
import cosplayin.app.posts.model.entity.PostsMedia;

public interface PostsMediaRepository extends JpaRepository<PostsMedia, UUID> {
    @Query("""
            SELECT new cosplayin.app.posts.model.dto.PostsMediaResponse(
                pm.id,
                pm.posts.id,
                pm.mediaUrl,
                pm.mediaType,
                pm.displayOrder,
                pm.createdAt
            )
            FROM PostsMedia pm
            WHERE pm.posts.id = :postId
            ORDER BY pm.displayOrder ASC
            """)
    List<PostsMediaResponse> findMediaByPostId(UUID postId);

    @Query("""
            SELECT new cosplayin.app.posts.model.dto.PostsMediaResponse(
                pm.id,
                pm.posts.id,
                pm.mediaUrl,
                pm.mediaType,
                pm.displayOrder,
                pm.createdAt
            )
            FROM PostsMedia pm
            WHERE pm.posts.id IN :postIds
            ORDER BY pm.posts.id, pm.displayOrder ASC
            """)
    List<PostsMediaResponse> findMediaByPostIds(List<UUID> postIds);

    List<PostsMedia> findByPosts(Posts posts);
}
