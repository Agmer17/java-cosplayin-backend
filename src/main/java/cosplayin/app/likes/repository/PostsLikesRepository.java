package cosplayin.app.likes.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cosplayin.app.likes.model.dto.DetailLikesResponseDto;
import cosplayin.app.likes.model.entity.PostsLikes;

public interface PostsLikesRepository extends JpaRepository<PostsLikes, UUID> {

    @Query("""
            SELECT new cosplayin.app.likes.model.dto.DetailLikesResponseDto(
                pl.id,
                pl.posts.id,
                new cosplayin.app.likes.model.dto.LikesAuthorDTO(
                    u.id,
                    u.username,
                    pr.displayName,
                    u.role,
                    pr.avatarUrl
                ),
                pl.createdAt
            )
            FROM PostsLikes pl
            JOIN pl.user u
            JOIN Profiles pr ON pr.id = u.id
            WHERE pl.posts.id = :postsId
            ORDER BY pl.createdAt DESC
            """)
    List<DetailLikesResponseDto> findAllDetailsByPostsId(UUID postsId);

    @Query("""
            SELECT pl.posts.id
            FROM PostsLikes pl
            WHERE pl.user.id = :userId
            """)
    List<UUID> findLikedPostsIdsByUserId(UUID userId);

    @Query("""
            SELECT pl.posts.id
            FROM PostsLikes pl
            WHERE pl.user.username = :username
            """)
    List<UUID> findLikedPostsIdsByUsername(String username);

    Optional<PostsLikes> findByPosts_IdAndUser_Id(UUID postsId, UUID userId);
}
