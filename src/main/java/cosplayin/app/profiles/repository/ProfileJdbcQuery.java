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
            p.gender,
            p.following_count,
            CASE
                WHEN CAST(:currentUserId AS UUID) IS NULL
                    THEN 'NONE'
                WHEN EXISTS (
                    SELECT 1
                    FROM followers f
                    WHERE f.follower_id = CAST(:currentUserId AS UUID)
                      AND f.following_id = u.id
                )
                    THEN 'FOLLOWING'
                WHEN EXISTS (
                    SELECT 1
                    FROM follow_requests fr
                    WHERE fr.requester_id = CAST(:currentUserId AS UUID)
                      AND fr.target_id = u.id
                )
                    THEN 'REQUESTED'
                ELSE 'NONE'
            END AS follow_status
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
        params.put("currentUserId", null);


        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(sql, params, mapper));

        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }

    }

    public Optional<DetailProfileDTO> findProfileDetailsByUsername(String username, UUID curr) {
        String sql = DETAIL_PROFILE_QUERY + """
                WHERE u.username = :username
                """;

        Map<String, Object> params = new HashMap<>();

        params.put("username", username);
        params.put("currentUserId", curr);


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
        params.put("currentUserId", null);

        return jdbcTemplate.query(sql, params, mapper);
    }

}
