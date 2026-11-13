package cosplayin.app.conversation.controller;

import cosplayin.app.conversation.model.dto.ConversationResponseDTO;
import cosplayin.app.conversation.service.ConversationService;
import cosplayin.app.core.response.SuccessResponse;
import cosplayin.app.security.anot.CurrentUser;
import cosplayin.app.security.anot.RequireAuth;
import cosplayin.app.security.context.UserCredentials;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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




}
