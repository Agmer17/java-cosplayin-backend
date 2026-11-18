package cosplayin.app.conversation.controller;

import cosplayin.app.conversation.model.dto.ConversationResponseDTO;
import cosplayin.app.conversation.model.dto.CreateConversationGroupDTO;
import cosplayin.app.conversation.model.entity.Conversation;
import cosplayin.app.conversation.service.ConversationService;
import cosplayin.app.core.authorization.UserStatus;
import cosplayin.app.core.response.SuccessResponse;
import cosplayin.app.security.anot.CurrentUser;
import cosplayin.app.security.anot.RequireAuth;
import cosplayin.app.security.anot.RequireUserStatus;
import cosplayin.app.security.context.UserCredentials;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequireAuth
@RequestMapping("/api/conversation")
public class ConversationController {
    private final ConversationService conversationService;


    @GetMapping("/me/latest")
    public ResponseEntity<SuccessResponse<List<ConversationResponseDTO>>> handleGetLatesChat(
            @CurrentUser UserCredentials cred
    ) {

        return ResponseEntity.ok(new SuccessResponse<>("successfully getting the latest chat data",
                conversationService.getLatestMessage(cred.getId())));
    }

    @PostMapping
    @RequireUserStatus({UserStatus.ACTIVE})
    public ResponseEntity<SuccessResponse<ConversationResponseDTO>> handleCreateConversation(
            @ModelAttribute CreateConversationGroupDTO dto,
            @CurrentUser UserCredentials cred
    ) {

        Conversation conversation = conversationService.createConversationGroup(cred.getId(), dto);
        ConversationResponseDTO responseDTO = conversationService.getConversationGroupByKey(conversation.getConvKey(),
                cred.getId());

        return ResponseEntity.ok(new SuccessResponse<>("successfully creating new group", responseDTO));
    }
}
