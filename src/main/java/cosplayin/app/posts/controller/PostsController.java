package cosplayin.app.posts.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import cosplayin.app.core.authorization.UserRoles;
import cosplayin.app.core.authorization.UserStatus;
import cosplayin.app.core.exception.model.ForbiddenAccessExceptions;
import cosplayin.app.core.response.SuccessResponse;
import cosplayin.app.posts.model.dto.CreatePostsDTO;
import cosplayin.app.posts.model.dto.PostsResponse;
import cosplayin.app.posts.service.PostsService;
import cosplayin.app.security.anot.CurrentUser;
import cosplayin.app.security.anot.RequireAuth;
import cosplayin.app.security.anot.RequireRole;
import cosplayin.app.security.anot.RequireUserStatus;
import cosplayin.app.security.context.UserCredentials;
import cosplayin.app.session.model.SessionDataModel;
import cosplayin.app.utils.SignerUrlUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.FileSystemResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PostsController {

        private final PostsService service;
        private final SignerUrlUtils signer;

        @PostMapping("/posts")
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

        @GetMapping("/posts/{postId}")
        public ResponseEntity<SuccessResponse<PostsResponse>> getPostsById(@PathVariable UUID postId,
                        HttpServletRequest request) {
                UUID viewerId = getViewerId(request);

                PostsResponse resp = service.getPostsDetail(postId, viewerId);

                return ResponseEntity.ok().body(SuccessResponse.<PostsResponse>builder()
                                .data(resp)
                                .message("successfully creating your posts")
                                .build());

        }

        @GetMapping("/posts/all")
        @RequireAuth
        @RequireRole({ UserRoles.ADMIN, UserRoles.MODERATOR })
        public ResponseEntity<SuccessResponse<List<PostsResponse>>> getAllPosts(@RequestParam int page,
                        @CurrentUser UserCredentials curr) {

                List<PostsResponse> resp = service.getAllPosts(page, curr.getId());
                return ResponseEntity.ok().body(SuccessResponse.<List<PostsResponse>>builder()
                                .data(resp)
                                .message("successfully creating your posts")
                                .build());
        }

        @GetMapping("/posts/feed")
        public ResponseEntity<SuccessResponse<List<PostsResponse>>> handleGetPostsFeed(
                        @RequestParam(defaultValue = "0", required = false) int page, HttpServletRequest request) {

                UUID viewerId = getViewerId(request);
                List<PostsResponse> resp = service.getPostsFeed(page, viewerId);
                return ResponseEntity.ok().body(SuccessResponse.<List<PostsResponse>>builder()
                                .data(resp)
                                .message("successfully creating your posts")
                                .build());
        }

        @DeleteMapping("/posts/{id}")
        @RequireAuth
        public ResponseEntity<SuccessResponse<Object>> handleDeletePosts(@PathVariable UUID id,
                        @CurrentUser UserCredentials cred) {

                service.deletePosts(id, cred);
                return ResponseEntity.ok().body(SuccessResponse.builder()
                                .message("successfully deleting the posts")
                                .data(null)
                                .build());

        }

        @GetMapping("/users/{username}/posts")
        public ResponseEntity<SuccessResponse<List<PostsResponse>>> handleGetPostsFromUsers(
                        @RequestParam(required = false, defaultValue = "0") int page,
                        @PathVariable String username,
                        HttpServletRequest request) {

                UUID viewerId = getViewerId(request);

                List<PostsResponse> resp = service.getPostsFromUsers(username, page, viewerId);
                return ResponseEntity.ok().body(SuccessResponse.<List<PostsResponse>>builder()
                                .data(resp)
                                .message("successfully creating your posts")
                                .build());
        }

        @GetMapping("/posts")
        public ResponseEntity<SuccessResponse<List<PostsResponse>>> handleSearchPosts(
                        @RequestParam(required = false, defaultValue = "0") int page,
                        @RequestParam(required = false, defaultValue = "") String query,
                        HttpServletRequest request) {
                UUID viewerId = getViewerId(request);

                List<PostsResponse> resp = service.searchPostsByKeyword(query, viewerId, page);
                return ResponseEntity.ok().body(SuccessResponse.<List<PostsResponse>>builder()
                                .data(resp)
                                .message("successfully creating your posts")
                                .build());

        }

        @GetMapping("/uploads/private/posts/media/{filename}")
        public ResponseEntity<Resource> getMethodName(@RequestParam String expires,
                        @RequestParam String sig,
                        @PathVariable String filename) {

                if (!signer.isValid("private/posts/media/" + filename, Long.parseLong(expires), sig)) {
                        throw new ForbiddenAccessExceptions("you can't access this posts media!");
                }
                String strPath = "uploads/private/posts/media/" + filename;
                Path path = Paths.get(strPath);

                Resource resource = new FileSystemResource(path);

                if (!resource.exists()) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
                }

                String contentType;
                try {
                        contentType = Files.probeContentType(path);
                } catch (IOException e) {
                        contentType = null;
                }

                MediaType mediaType = contentType != null
                                ? MediaType.parseMediaType(contentType)
                                : MediaType.APPLICATION_OCTET_STREAM;

                return ResponseEntity.ok()
                                .contentType(mediaType)
                                .body(resource);
        }

        private UUID getViewerId(HttpServletRequest request) {
                SessionDataModel session = (SessionDataModel) request.getAttribute("session");

                return session != null ? session.getId() : null;
        }

}
