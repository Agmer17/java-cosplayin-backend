package cosplayin.app.followers.repository.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import cosplayin.app.core.authorization.UserRoles;
import cosplayin.app.followers.model.dto.FollowerResponse;
import cosplayin.app.followers.model.type.FollowStatus;
import cosplayin.app.profiles.model.dto.SimpleProfileDTO;
import cosplayin.app.profiles.model.type.ProfilesVisibility;

@Component
public class FollowerResponseMapper implements RowMapper<FollowerResponse> {

    @Override
    public FollowerResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new FollowerResponse(
                SimpleProfileDTO.builder()
                        .id(rs.getObject("user_id", UUID.class))
                        .displayName(rs.getString("display_name"))
                        .avatarUrl(rs.getString("avatar_url"))
                        .visibility(ProfilesVisibility.valueOf(rs.getString("visibility")))
                        .username(rs.getString("username"))
                        .userRole(UserRoles.valueOf(rs.getString("user_role")))
                        .build(),
                FollowStatus.valueOf(rs.getString("follow_status")),
                rs.getObject("created_at", LocalDateTime.class));
    }

}
