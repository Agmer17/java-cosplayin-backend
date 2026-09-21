package cosplayin.app.session.model;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SessionDataModel {
    private UUID id;
    private LocalDateTime issuedAt;
    private LocalDateTime expiredAt;
}
