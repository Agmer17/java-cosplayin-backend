package cosplayin.app.cosplayevent.repository.mapper;

import cosplayin.app.cosplayevent.model.dto.CosplayEventLocationResponseDTO;
import cosplayin.app.cosplayevent.model.dto.CosplayEventResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CosplayEvenetResponseMapper implements RowMapper<CosplayEventResponseDTO> {
    private final ObjectMapper objectMapper;

    @Override
    public CosplayEventResponseDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new CosplayEventResponseDTO(
                rs.getObject("id", UUID.class),
                rs.getString("title"),
                rs.getString("description"),
                rs.getString("media_url"),
                rs.getObject("start_at", LocalDateTime.class),
                rs.getObject("end_at", LocalDateTime.class),
                rs.getString("event_information_url"),
                parseLocation(rs.getString("location")),
                rs.getObject("created_at", LocalDateTime.class)
        );
    }

    private CosplayEventLocationResponseDTO parseLocation(String json) {
        if (json == null) {
            return null;
        }
        try {
            return objectMapper.readValue(json, CosplayEventLocationResponseDTO.class);
        } catch (JacksonException e) {
            throw new IllegalStateException("Gagal parse location JSON: " + json, e);
        }
    }

}
