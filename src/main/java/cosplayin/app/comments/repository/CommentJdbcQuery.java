package cosplayin.app.comments.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import cosplayin.app.comments.model.dto.CommentResponse;
import cosplayin.app.comments.model.mapper.CommentMapper;
import cosplayin.app.core.exception.model.NotFoundException;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CommentJdbcQuery {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final CommentMapper commentResponseMapper;

    private final String COMMENT_QUERY_RESPONSE = """
            SELECT
                c.id,
                c.comment,
                c.parent_comment_id as parent_id,
                p.avatar_url,
                u.username,
                pd.id as posts_id,
                c.reply_count,
                c.like_count,
                EXISTS(
                    SELECT 1
                    FROM comments_like cl
                    WHERE cl.comment_id = c.id
                        AND cl.user_id = :userId
                ) as is_liked
            FROM post_comment c
            JOIN profiles p on p.id = c.user_id
            JOIN users u on u.id = c.user_id
            JOIN posts pd on pd.id = c.posts_id
            """;

    public Optional<CommentResponse> getByCommentId(UUID id, UUID curr) {
        String sql = COMMENT_QUERY_RESPONSE + """
                WHERE c.id = :id
                """;

        try {
            Map<String, Object> params = new HashMap<>();

            params.put("id", id);
            params.put("userId", curr);

            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(sql, params, commentResponseMapper));

        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("comment not found!!");
        }
    }

    public List<CommentResponse> getCommentFromPosts(UUID postsId, UUID curr) {
        String sql = COMMENT_QUERY_RESPONSE + """
                WHERE pd.id = :id AND c.parent_comment_id IS NULL
                """;

        try {
            Map<String, Object> params = new HashMap<>();

            params.put("id", postsId);
            params.put("userId", curr);

            return jdbcTemplate.query(sql, params, commentResponseMapper);

        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("comment not found!!");
        }
    }

    public List<CommentResponse> getReplyFrom(UUID commentId, UUID curr) {

        String sql = COMMENT_QUERY_RESPONSE + """
                where c.parent_comment_id = :id
                """;
        try {
            Map<String, Object> params = new HashMap<>();

            params.put("id", commentId);
            params.put("userId", curr);

            return jdbcTemplate.query(sql, params, commentResponseMapper);

        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("comment not found!!");
        }
    }

}
