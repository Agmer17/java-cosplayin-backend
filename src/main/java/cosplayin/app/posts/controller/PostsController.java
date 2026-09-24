package cosplayin.app.posts.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cosplayin.app.core.authorization.UserStatus;
import cosplayin.app.core.response.SuccessResponse;
import cosplayin.app.posts.model.dto.CreatePostsDTO;
import cosplayin.app.posts.model.dto.PostsResponse;
import cosplayin.app.posts.service.PostsService;
import cosplayin.app.security.anot.CurrentUser;
import cosplayin.app.security.anot.RequireAuth;
import cosplayin.app.security.anot.RequireUserStatus;
import cosplayin.app.security.context.UserCredentials;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostsController {

    private final PostsService service;

    @PostMapping("/create")
    @RequireAuth
    @RequireUserStatus({ UserStatus.ACTIVE })
    public ResponseEntity<SuccessResponse<PostsResponse>> postCreateNew(@Valid @ModelAttribute CreatePostsDTO dto,
            @CurrentUser UserCredentials cred) {

        PostsResponse response = service.createPosts(dto, cred.getId());
        return ResponseEntity.ok().body(SuccessResponse.<PostsResponse>builder()
                .data(response)
                .message("successfully creating your posts")
                .build());
    }

    @GetMapping("/id/{postId}")
    public ResponseEntity<SuccessResponse<PostsResponse>> getPostsById(@PathVariable UUID postId) {
        PostsResponse resp = service.getPostsDetail(postId, null);

        return ResponseEntity.ok().body(SuccessResponse.<PostsResponse>builder()
                .data(resp)
                .message("successfully creating your posts")
                .build());

    }

}
