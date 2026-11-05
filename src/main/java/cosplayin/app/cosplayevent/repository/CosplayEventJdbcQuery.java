package cosplayin.app.cosplayevent.repository;

import cosplayin.app.cosplayevent.model.dto.CosplayEventResponseDTO;
import cosplayin.app.cosplayevent.repository.mapper.CosplayEvenetResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class CosplayEventJdbcQuery {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final CosplayEvenetResponseMapper rowMapper;

    private static final String EVENTS_PROJECTION = """
            SELECT
                e.id,
                e.title,
                e.description,
                e.media_url,
                e.event_information_url,
                e.start_at,
                e.end_at,
                e.created_at,
                jsonb_build_object(
                    'id',            l.id,
                    'name',          l.name,
                    'address',       l.address,
                    'location_type', l.location_type,
                    'osm_id',        l.osm_id,
                    'osm_type',      l.osm_type,
                    'latitude',      ST_Y(l.location),
                    'longitude',     ST_X(l.location)
                ) AS location
            FROM cosplay_events e
            JOIN cosplay_event_locations l ON l.id = e.event_location_id
            """;

    public Optional<CosplayEventResponseDTO> findById(UUID id) {
        String sql = EVENTS_PROJECTION + """
                WHERE e.id = :eventId
                """;

        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(sql, Map.of("eventId", id), rowMapper));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<CosplayEventResponseDTO> findByBoundingBox(
            double minLon,
            double minLat,
            double maxLon,
            double maxLat,
            String search
    ) {
        StringBuilder sql = new StringBuilder(EVENTS_PROJECTION).append("""
        WHERE l.location && ST_MakeEnvelope(:minLon, :minLat, :maxLon, :maxLat, 4326)
          AND e.end_at >= :now
        """);

        Map<String, Object> params = new HashMap<>();
        params.put("minLon", minLon);
        params.put("minLat", minLat);
        params.put("maxLon", maxLon);
        params.put("maxLat", maxLat);
        params.put("now", LocalDateTime.now());

        if (search != null && !search.isBlank()) {
            sql.append("""
        AND (
            e.title ILIKE :search
            OR l.name ILIKE :search
            OR l.address ILIKE :search
        )
        """);

            params.put("search", "%" + escapeLike(search.trim()) + "%");
        }

        sql.append(" ORDER BY e.start_at");

        return jdbcTemplate.query(sql.toString(), params, rowMapper);
    }

    private String escapeLike(String input) {
        return input
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }
}
