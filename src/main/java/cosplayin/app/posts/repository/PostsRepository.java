package cosplayin.app.posts.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cosplayin.app.posts.model.dto.PostsResponse;
import cosplayin.app.posts.model.entity.Posts;

public interface PostsRepository extends JpaRepository<Posts, UUID> {

    @Query("""
            SELECT new cosplayin.app.posts.model.dto.PostsResponse(
                p.id,
                new cosplayin.app.profiles.model.dto.DetailProfileDTO(
                    pr.id,
                    pr.displayName,
                    pr.bio,
                    pr.avatarUrl,
                    pr.bannerUrl,
                    pr.visibility,
                    u.username,
                    u.status,
                    u.role
                ),
                p.caption,
                p.status,
                p.likeCount,
                p.bookmarkCount,
                p.shareCount,
                p.createdAt,
                null
            )
            FROM Posts p
            JOIN p.author u
            JOIN Profiles pr ON pr.id = u.id
            WHERE p.id = :id
            """)
    Optional<PostsResponse> findPostDetailsById(UUID id);

    @Query("""
            SELECT new cosplayin.app.posts.model.dto.PostsResponse(
                p.id,
                new cosplayin.app.profiles.model.dto.DetailProfileDTO(
                    pr.id,
                    pr.displayName,
                    pr.bio,
                    pr.avatarUrl,
                    pr.bannerUrl,
                    pr.visibility,
                    u.username,
                    u.status,
                    u.role
                ),
                p.caption,
                p.status,
                p.likeCount,
                p.bookmarkCount,
                p.shareCount,
                p.createdAt,
                null
            )
            FROM Posts p
            JOIN p.author u
            JOIN Profiles pr ON pr.id = u.id
            """)
    Page<PostsResponse> findAllPostsWithDetails(Pageable pageable);
}
