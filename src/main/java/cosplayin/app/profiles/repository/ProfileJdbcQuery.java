package cosplayin.app.profiles.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import cosplayin.app.profiles.model.dto.DetailProfileDTO;
import cosplayin.app.profiles.repository.mapper.DetailProfileMapper;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProfileJdbcQuery {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final String DETAIL_PROFILE_QUERY = """
            SELECT
            u.id,
            p.display_name,
            p.bio,
            p.avatar_url,
            p.banner_url,
            p.visibility,
            p.follower_count,
            u.username,
            u.status,
            u.role,
            p.gender
            FROM profiles p
            JOIN users u ON u.id = p.id
            """;

    private final DetailProfileMapper mapper;

    public Optional<DetailProfileDTO> findProfileDetailsById(UUID id) {
        String sql = DETAIL_PROFILE_QUERY + """
                WHERE p.id = :id
                """;

        Map<String, Object> params = new HashMap<>();

        params.put("id", id);

        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(sql, params, mapper));

        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }

    }

    public Optional<DetailProfileDTO> findProfileDetailsByUsername(String username) {
        String sql = DETAIL_PROFILE_QUERY + """
                WHERE u.username = :username
                """;

        Map<String, Object> params = new HashMap<>();

        params.put("username", username);

        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(sql, params, mapper));

        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<DetailProfileDTO> findDiscoverProfiles(UUID curr, int page, int limit) {
        String sql = DETAIL_PROFILE_QUERY + """
                 WHERE p.id <> :currentUserId
                 ORDER BY RANDOM()
                 LIMIT :limit
                 OFFSET :page
                """;

        Map<String, Object> params = new HashMap<>();

        params.put("currentUserId", curr);
        params.put("page", page);
        params.put("limit", limit);

        return jdbcTemplate.query(sql, params, mapper);
    }

    public List<DetailProfileDTO> searchProfiles(String query) {
        String sql = DETAIL_PROFILE_QUERY + """
                WHERE u.username ILIKE CONCAT('%', :query, '%')
                    OR p.display_name ILIKE CONCAT('%', :query, '%')
                """;

        Map<String, Object> params = new HashMap<>();

        params.put("query", query);

        return jdbcTemplate.query(sql, params, mapper);
    }

}
