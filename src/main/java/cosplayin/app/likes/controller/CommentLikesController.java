package cosplayin.app.likes.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cosplayin.app.core.authorization.UserStatus;
import cosplayin.app.core.response.SuccessResponse;
import cosplayin.app.likes.service.CommentLikesService;
import cosplayin.app.security.anot.CurrentUser;
import cosplayin.app.security.anot.RequireAuth;
import cosplayin.app.security.anot.RequireUserStatus;
import cosplayin.app.security.context.UserCredentials;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/comment/{id}/likes")
@RequiredArgsConstructor
public class CommentLikesController {

    private final CommentLikesService commentLikesService;

    @PostMapping
    @RequireAuth
    @RequireUserStatus({ UserStatus.ACTIVE })
    public ResponseEntity<SuccessResponse<LocalDateTime>> postsCreateLikes(
            @PathVariable UUID id,
            @CurrentUser UserCredentials curr) {
        LocalDateTime created = commentLikesService.createCommentsLike(curr.getId(), id);
        return ResponseEntity.ok().body(
                new SuccessResponse<LocalDateTime>("suceessfully creating the lieks", created));
    }

    @DeleteMapping
    @RequireAuth
    @RequireUserStatus({ UserStatus.ACTIVE })
    public ResponseEntity<SuccessResponse<Object>> handleDeleteCommetLikes(@PathVariable UUID id,
            @CurrentUser UserCredentials curr) {

        commentLikesService.removeCommentLikes(curr.getId(), id);
        return ResponseEntity.ok().body(
                new SuccessResponse<Object>("successfully removing the likes from comments", null));
    }
}
