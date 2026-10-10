package cosplayin.app.followers.repository;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FollowerJdbcQuery {

    private final NamedParameterJdbcTemplate jdbcTemplate;

}
