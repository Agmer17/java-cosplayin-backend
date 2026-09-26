package cosplayin.app.likes.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cosplayin.app.core.authorization.UserStatus;
import cosplayin.app.core.response.SuccessResponse;
import cosplayin.app.likes.model.dto.DetailLikesResponseDto;
import cosplayin.app.likes.service.PostsLIkesService;
import cosplayin.app.posts.model.dto.PostsResponse;
import cosplayin.app.security.anot.CurrentUser;
import cosplayin.app.security.anot.RequireAuth;
import cosplayin.app.security.anot.RequireUserStatus;
import cosplayin.app.security.context.UserCredentials;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/post-likes")
@RequiredArgsConstructor
public class PostsLikesController {

        private final PostsLIkesService service;

        @GetMapping("/post/{postsId}")
        public ResponseEntity<SuccessResponse<List<DetailLikesResponseDto>>> handleGetLikeFromPosts(
                        @PathVariable UUID postsId) {
                return ResponseEntity.ok().body(SuccessResponse.<List<DetailLikesResponseDto>>builder()
                                .data(service.getLikesDetailFromPosts(postsId))
                                .message("successfully retrieving likes data from posts")
                                .build());
        }

        @PostMapping("/post/{postsId}")
        @RequireAuth
        @RequireUserStatus({ UserStatus.ACTIVE })
        public ResponseEntity<SuccessResponse<LocalDateTime>> handlePostsCreateLikes(
                        @PathVariable UUID postsId,
                        @CurrentUser UserCredentials cred) {

                System.out.println("POSTS ID NYA ADALAH : " + postsId);
                return ResponseEntity.ok().body(SuccessResponse.<LocalDateTime>builder()
                                .message("successfully creating the likes")
                                .data(service.createLike(postsId, cred.getId()))
                                .build());
        }

        @GetMapping("/user/{username}")
        @RequireAuth
        public ResponseEntity<SuccessResponse<List<PostsResponse>>> handleGetLikedPostsFromUsers(
                        @PathVariable String username,
                        @CurrentUser UserCredentials cred) {

                return ResponseEntity.ok().body(SuccessResponse.<List<PostsResponse>>builder()
                                .message("successfully getting liked posts from " + username)
                                .data(service.getLikedPostsFromUsers(username, cred.getId()))
                                .build());
        }

        @GetMapping("/me")
        public ResponseEntity<SuccessResponse<List<PostsResponse>>> handleGetMyLikedPosts(
                        @CurrentUser UserCredentials cred) {
                return ResponseEntity.ok().body(SuccessResponse.<List<PostsResponse>>builder()
                                .message("successfully your liked posts")
                                .data(service.getLikedPostsFromUsers(cred.getId()))
                                .build());
        }

        @DeleteMapping("/id/{likesId}")
        @RequireAuth
        public ResponseEntity<SuccessResponse<Object>> handleDeleteLikes(@PathVariable UUID likesId,
                        @CurrentUser UserCredentials cred) {

                service.deleteLike(cred.getId(), likesId);
                return ResponseEntity.ok().body(SuccessResponse.builder()
                                .message("successfully delete your likes from the posts")
                                .data(null)
                                .build());

        }

}
