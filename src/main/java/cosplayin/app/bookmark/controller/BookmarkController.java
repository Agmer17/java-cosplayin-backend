package cosplayin.app.bookmark.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cosplayin.app.bookmark.service.BookmarkService;
import cosplayin.app.core.authorization.UserStatus;
import cosplayin.app.core.response.SuccessResponse;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService service;

    @PostMapping("/posts/{postId}/bookmark")
    @RequireAuth
    @RequireUserStatus({ UserStatus.ACTIVE })
    public ResponseEntity<SuccessResponse<LocalDateTime>> handleCreateBookmark(@PathVariable UUID postId,
            @CurrentUser UserCredentials ctx) {

        return ResponseEntity.ok().body(SuccessResponse.<LocalDateTime>builder()
                .data(service.createBookmark(postId, ctx.getId()))
                .message("successfully getting the bookmark data!")
                .build());
    }

    @GetMapping("/profiles/me/bookmark")
    @RequireAuth
    public ResponseEntity<SuccessResponse<List<PostsResponse>>> getMyBookmark(@CurrentUser UserCredentials ctx) {
        return ResponseEntity.ok().body(SuccessResponse.<List<PostsResponse>>builder()
                .data(service.findBookmarkFromUsers(ctx.getId(), ctx.getId()))
                .message("successfully getting your bookmark data")
                .build());
    }

    @DeleteMapping("/posts/{postsId}/bookmark")
    public ResponseEntity<SuccessResponse<Object>> handleDeleteBookmark(@PathVariable UUID postsId,
            @CurrentUser UserCredentials ctx) {
        service.deleteBookmarks(postsId, ctx.getId());
        return ResponseEntity.ok().body(SuccessResponse.<Object>builder()
                .data(null)
                .build());
    }
}
