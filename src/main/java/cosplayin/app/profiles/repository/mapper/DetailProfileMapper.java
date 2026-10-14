package cosplayin.app.profiles.repository.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import cosplayin.app.core.authorization.UserRoles;
import cosplayin.app.core.authorization.UserStatus;
import cosplayin.app.profiles.model.dto.DetailProfileDTO;
import cosplayin.app.profiles.model.type.Gender;
import cosplayin.app.profiles.model.type.ProfilesVisibility;

@Component
public class DetailProfileMapper implements RowMapper<DetailProfileDTO> {

    @Override
    public DetailProfileDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return DetailProfileDTO.builder()
                .id(rs.getObject("id", UUID.class))
                .displayName(rs.getString("display_name"))
                .bio(rs.getString("bio"))
                .avatarUrl(rs.getString("avatar_url"))
                .bannerUrl(rs.getString("banner_url"))
                .visibility(ProfilesVisibility.valueOf(rs.getString("visibility")))
                .username(rs.getString("username"))
                .userStatus(UserStatus.valueOf(rs.getString("status")))
                .userRole(UserRoles.valueOf(rs.getString("role")))
                .followerCount(rs.getInt("follower_count"))
                .gender(Gender.valueOf(rs.getString("gender")))
                .build();
    }

}
