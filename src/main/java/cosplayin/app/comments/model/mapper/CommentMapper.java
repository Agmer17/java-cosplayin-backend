package cosplayin.app.comments.model.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import cosplayin.app.comments.model.dto.CommentResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CommentMapper implements RowMapper<CommentResponse> {

    @Override
    public CommentResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
        try {
            return CommentResponse.builder()
                    .id(rs.getObject("id", UUID.class))
                    .comment(rs.getString("comment"))
                    .parentId(rs.getObject("parent_id", UUID.class))
                    .profilePicture(rs.getString("avatar_url"))
                    .username(rs.getString("username"))
                    .postsId(rs.getObject("posts_id", UUID.class))
                    .replyCount(rs.getInt("reply_count"))
                    .likeCount(rs.getInt("like_count"))
                    .build();
        } catch (Exception e) {
            throw new SQLException("cannot mapping the row from the datavase : ", e.getMessage());
        }
    }

}
