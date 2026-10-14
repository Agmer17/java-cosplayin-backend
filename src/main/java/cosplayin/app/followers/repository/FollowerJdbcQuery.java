package cosplayin.app.followers.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import cosplayin.app.followers.model.dto.FollowerResponse;
import cosplayin.app.followers.repository.mapper.FollowerResponseMapper;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FollowerJdbcQuery {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final FollowerResponseMapper mapper;
    private static final String BASE_FOLLOWER_QUERY = """
            SELECT
                u.id              AS user_id,
                p.display_name    AS display_name,
                p.avatar_url      AS avatar_url,
                p.visibility      AS visibility,
                u.username        AS username,
                u.role            AS user_role,
                f.created_at      AS created_at,
                CASE
                    WHEN vf.id IS NOT NULL THEN 'FOLLOWING'
                    WHEN vr.id IS NOT NULL THEN 'REQUESTED'
                    ELSE 'NONE'
                END               AS follow_status
            FROM followers f
            JOIN users u     ON u.id = f.%s
            JOIN profiles p  ON p.id = u.id
            LEFT JOIN followers vf
                   ON vf.follower_id  = :viewerId
                  AND vf.following_id = u.id
            LEFT JOIN follow_requests vr
                   ON vr.requester_id = :viewerId
                  AND vr.target_id    = u.id
            """;

    public List<FollowerResponse> findFollowerFrom(UUID target, UUID viewerId) {
        String sql = BASE_FOLLOWER_QUERY.formatted("follower_id")
                + " WHERE f.following_id = :targetId ORDER BY f.created_at DESC";

        Map<String, Object> params = new HashMap<>();

        params.put("targetId", target);
        params.put("viewerId", viewerId);

        return jdbcTemplate.query(sql, params, mapper);
    }


    public List<FollowerResponse> findFollowingFrom(UUID target, UUID viewerId) {
        String sql = BASE_FOLLOWER_QUERY.formatted("following_id")
                + " WHERE f.follower_id = :targetId ORDER BY f.created_at DESC";

        Map<String, Object> params = new HashMap<>();

        params.put("targetId", target);
        params.put("viewerId", viewerId);

        return jdbcTemplate.query(sql, params, mapper);
    }



}
