package cosplayin.app.conversation.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateConversationGroupDTO {
    private List<UUID> initialMember;
    private String groupName;
    private MultipartFile groupImage;

}
