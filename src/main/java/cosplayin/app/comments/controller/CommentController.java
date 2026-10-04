package cosplayin.app.comments.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cosplayin.app.comments.model.dto.CommentResponse;
import cosplayin.app.comments.model.dto.CreateCommentDto;
import cosplayin.app.comments.service.PostsCommentService;
import cosplayin.app.core.authorization.UserStatus;
import cosplayin.app.core.response.SuccessResponse;
import cosplayin.app.security.anot.CurrentUser;
import cosplayin.app.security.anot.RequireAuth;
import cosplayin.app.security.anot.RequireUserStatus;
import cosplayin.app.security.context.UserCredentials;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

        private final PostsCommentService commentService;

        @PostMapping("/posts/{id}/comment")
        @RequireAuth
        @RequireUserStatus({ UserStatus.ACTIVE })
        public ResponseEntity<SuccessResponse<CommentResponse>> handelPostsCreateComment(
                        @RequestBody CreateCommentDto dto,
                        @PathVariable UUID id,
                        @CurrentUser UserCredentials cred) {

                CommentResponse saved = commentService.createComment(dto, cred.getId(), id);

                return ResponseEntity.ok().body(
                                SuccessResponse.<CommentResponse>builder()
                                                .message("successfully creating a comment")
                                                .data(saved)
                                                .build());
        }

        @GetMapping("/posts/{id}/comment")
        public ResponseEntity<SuccessResponse<List<CommentResponse>>> getCOmmentFromPosts(
                        @PathVariable UUID id) {
                return ResponseEntity.ok().body(
                                SuccessResponse.<List<CommentResponse>>builder()
                                                .message("successfully getting the comment from the posts")
                                                .data(commentService.getCommentResponseFromPosts(id))
                                                .build());
        }

        @GetMapping("/comment/{id}/replies")
        public ResponseEntity<SuccessResponse<List<CommentResponse>>> getCommentReplies(@PathVariable UUID id) {
                System.out.println("ID YG DIKIRIM : " + id);
                return ResponseEntity.ok().body(
                                SuccessResponse.<List<CommentResponse>>builder()
                                                .message("successfully getting the comment from the posts")
                                                .data(commentService.getReplyFromComment(id))
                                                .build());
        }

        @DeleteMapping("/comment/{id}")
        @RequireAuth
        @RequireUserStatus({ UserStatus.ACTIVE })
        public ResponseEntity<SuccessResponse<Object>> handleDeteleComment(@PathVariable UUID id,
                        @CurrentUser UserCredentials cred) {

                commentService.deleteComment(id, cred);
                return ResponseEntity.ok().body(
                                SuccessResponse.builder()
                                                .message("successfully deleteing the comment")
                                                .data(null)
                                                .build());
        }

}
