package cosplayin.app.message.controller;

import cosplayin.app.core.authorization.UserStatus;
import cosplayin.app.core.response.SuccessResponse;
import cosplayin.app.message.model.dto.CreateMessageDTO;
import cosplayin.app.message.model.dto.MessageResponseDTO;
import cosplayin.app.message.service.MessageService;
import cosplayin.app.security.anot.CurrentUser;
import cosplayin.app.security.anot.RequireAuth;
import cosplayin.app.security.anot.RequireUserStatus;
import cosplayin.app.security.context.UserCredentials;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
@RequireAuth
public class MessageController {
    private final MessageService service;

    @PostMapping("/direct/{userId}")
    @RequireAuth
    @RequireUserStatus({UserStatus.ACTIVE})
    public ResponseEntity<SuccessResponse<MessageResponseDTO>> postCreatePrivateMessage(
            @PathVariable UUID userId,
            @CurrentUser UserCredentials cred,
            @ModelAttribute CreateMessageDTO dto
    ) {

        return ResponseEntity.ok(new SuccessResponse<>("successfully creating a message",
                service.createPrivateMessage(userId, cred.getId(), dto)));
    }

    @GetMapping("/direct/{userId}")
    @RequireAuth
    @RequireUserStatus({UserStatus.ACTIVE})
    public ResponseEntity<SuccessResponse<List<MessageResponseDTO>>> handleGetPrivateMessage(
            @PathVariable UUID userId,
            @CurrentUser UserCredentials cred
    ) {
        return ResponseEntity.ok(new SuccessResponse<>("successfully getting the data",
                service.getPrivateMessage(cred.getId(), userId)
        ));
    }


    @PostMapping("/group/{groupId}")
    @RequireUserStatus({UserStatus.ACTIVE})
    public ResponseEntity<SuccessResponse<MessageResponseDTO>> handleCreateGroupMessaage(
            @CurrentUser UserCredentials cred,
            @ModelAttribute CreateMessageDTO dto,
            @PathVariable UUID groupId
    ) {
        return ResponseEntity.ok(new SuccessResponse<>("successfully sending the group message",
                service.createGroupMessage(groupId, cred.getId(), dto)
        ));

    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<SuccessResponse<List<MessageResponseDTO>>> handleGetGroupChatMessage(
            @CurrentUser UserCredentials cred,
            @PathVariable UUID groupId
    ) {
        return ResponseEntity.ok(new SuccessResponse<>("successfully getting the group chat message",
                service.getGroupChatMessage(groupId, cred.getId())));
    }
}
