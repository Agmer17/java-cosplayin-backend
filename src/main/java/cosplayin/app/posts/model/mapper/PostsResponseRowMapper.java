package cosplayin.app.posts.model.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import cosplayin.app.posts.model.dto.PostsMediaResponse;
import cosplayin.app.posts.model.dto.PostsResponse;
import cosplayin.app.posts.model.type.PostsCommentStatus;
import cosplayin.app.posts.model.type.PostsStatus;
import cosplayin.app.profiles.model.dto.DetailProfileDTO;
import lombok.RequiredArgsConstructor;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class PostsResponseRowMapper implements RowMapper<PostsResponse> {

        private final ObjectMapper mapper;

        @Override
        public PostsResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
                try {
                        return PostsResponse.builder()
                                        .postsId(rs.getObject("id", UUID.class))
                                        .author(
                                                        mapper.readValue(
                                                                        rs.getString("author"),
                                                                        DetailProfileDTO.class))
                                        .caption(rs.getString("caption"))
                                        .status(
                                                        PostsStatus.valueOf(
                                                                        rs.getString("status")))
                                        .commentAvailability(
                                                        PostsCommentStatus.valueOf(
                                                                        rs.getString("comment_availability")))
                                        .likeCount(rs.getLong("like_count"))
                                        .bookmarkCount(rs.getLong("bookmark_count"))
                                        .shareCount(rs.getLong("share_count"))
                                        .isLiked(rs.getBoolean("is_liked"))
                                        .isBookmarked(rs.getBoolean("is_bookmarked"))
                                        .createdAt(
                                                        rs.getObject(
                                                                        "created_at",
                                                                        LocalDateTime.class))
                                        .media(
                                                        mapper.readValue(
                                                                        rs.getString("media"),
                                                                        new TypeReference<List<PostsMediaResponse>>() {
                                                                        }))
                                        .build();

                } catch (Exception e) {
                        throw new SQLException("Failed to map PostsResponse", e);
                }
        }
}