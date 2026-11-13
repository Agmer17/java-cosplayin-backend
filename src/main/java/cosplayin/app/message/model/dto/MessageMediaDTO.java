package cosplayin.app.message.model.dto;


import cosplayin.app.utils.storage.type.SupportedFileType;
import lombok.*;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MessageMediaDTO {
    private UUID id;
    private String mediaUrl;
    private SupportedFileType mediaType;
    private Integer mediaOrder;
}
