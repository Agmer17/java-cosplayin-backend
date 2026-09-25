package cosplayin.app.posts.repository;

import cosplayin.app.posts.model.mapper.PostsResponseRowMapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import cosplayin.app.posts.model.dto.PostsResponse;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PostsJdbcDao {
    private final PostsResponseRowMapper postsResponseRowMapper;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    private static final String POSTS_PROJECTION = """
                SELECT
                    p.id,
                    json_build_object(
                        'id', ap.id,
                        'display_name', ap.display_name,
                        'bio', ap.bio,
                        'avatar_url', ap.avatar_url,
                        'banner_url', ap.banner_url,
                        'visibility', ap.visibility,
                        'username', u.username,
                        'user_status', u.status,
                        'user_role', u.role
                    ) AS author,
                    p.caption,
                    p.status,
                    p.comment_availability,
                    p.like_count,
                    p.bookmark_count,
                    p.share_count,
                    p.created_at,
                    COALESCE(pm.media, '[]'::json) AS media
                FROM posts p
                JOIN profiles ap ON ap.id = p.author_id
                JOIN users u ON u.id = ap.id
                LEFT JOIN LATERAL (
                    SELECT json_agg(
                        json_build_object(
                            'media_id', m.id,
                            'posts_id', m.posts_id,
                            'media_url', m.media_url,
                            'media_type', m.media_type,
                            'display_order', m.display_order,
                            'created_at', m.created_at
                        )
                        ORDER BY m.display_order
                    ) AS media
                    FROM post_media m
                    WHERE m.posts_id = p.id
                ) pm ON TRUE
            """;

    public Optional<PostsResponse> getPostsDetailById(UUID id) {
        String sql = POSTS_PROJECTION + """
                    WHERE p.id = :postId
                """;

        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            sql,
                            Map.of("postId", id),
                            postsResponseRowMapper));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<PostsResponse> findAllPostsWithDetails(
            int limit,
            long offset) {

        String sql = POSTS_PROJECTION + """
                    ORDER BY p.created_at DESC
                    LIMIT :limit
                    OFFSET :offset
                """;

        return jdbcTemplate.query(
                sql,
                Map.of(
                        "limit", limit,
                        "offset", offset),
                postsResponseRowMapper);
    }

    public List<PostsResponse> findRandomPosts(
            int limit,
            long offset) {

        String sql = POSTS_PROJECTION + """
                    WHERE p.status <> 'HIDDEN'
                    ORDER BY RANDOM()
                    LIMIT :limit
                    OFFSET :offset
                """;

        return jdbcTemplate.query(
                sql,
                Map.of(
                        "limit", limit,
                        "offset", offset),
                postsResponseRowMapper);
    }

    public List<PostsResponse> findAllByUsername(
            String username,
            int limit,
            long offset) {

        String sql = POSTS_PROJECTION + """
                    WHERE u.username = :username
                      AND p.status = 'VISIBLE'
                    ORDER BY p.created_at DESC
                    LIMIT :limit
                    OFFSET :offset
                """;

        return jdbcTemplate.query(
                sql,
                Map.of(
                        "username", username,
                        "limit", limit,
                        "offset", offset),
                postsResponseRowMapper);
    }

    public List<PostsResponse> searchPosts(
            String keyword,
            int limit,
            long offset) {

        String sql = POSTS_PROJECTION + """
                    WHERE p.status = 'VISIBLE'
                      AND (
                          p.caption ILIKE :query
                          OR u.username ILIKE :query
                          OR ap.display_name ILIKE :query
                      )
                    ORDER BY p.created_at DESC
                    LIMIT :limit
                    OFFSET :offset
                """;

        return jdbcTemplate.query(
                sql,
                Map.of(
                        "query", "%" + keyword + "%",
                        "limit", limit,
                        "offset", offset),
                postsResponseRowMapper);
    }
}
