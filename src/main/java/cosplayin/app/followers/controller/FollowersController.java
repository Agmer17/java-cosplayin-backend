package cosplayin.app.followers.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cosplayin.app.core.authorization.UserStatus;
import cosplayin.app.core.response.SuccessResponse;
import cosplayin.app.followers.model.dto.FollowerResponse;
import cosplayin.app.followers.service.FollowerService;
import cosplayin.app.security.anot.CurrentUser;
import cosplayin.app.security.anot.RequireAuth;
import cosplayin.app.security.anot.RequireUserStatus;
import cosplayin.app.security.context.UserCredentials;
import cosplayin.app.utils.GetOptionalCurrentUsers;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/{targetId}")
public class FollowersController {

        private final FollowerService service;

        @PostMapping("/follow")
        @RequireAuth
        @RequireUserStatus({ UserStatus.ACTIVE })
        public ResponseEntity<SuccessResponse<FollowerResponse>> handlePostsFollow(
                        @CurrentUser UserCredentials curr,
                        @PathVariable UUID targetId) {

                FollowerResponse resp = service.createFollow(curr.getId(), targetId);

                return ResponseEntity.ok().body(
                                new SuccessResponse<FollowerResponse>("successfully following this users", resp));
        }

        @DeleteMapping("/follow")
        @RequireAuth
        @RequireUserStatus({ UserStatus.ACTIVE })
        public ResponseEntity<SuccessResponse<Object>> handleUnfollow(
                        @CurrentUser UserCredentials curr,
                        @PathVariable UUID targetId) {

                service.deleteFollow(curr.getId(), targetId);

                return ResponseEntity.ok().body(
                                new SuccessResponse<Object>("successfully unfollowing this users", null));
        }

        @DeleteMapping("/follow-requests")
        @RequireAuth
        @RequireUserStatus({ UserStatus.ACTIVE })
        public ResponseEntity<SuccessResponse<Object>> handleDeleteFollowRequests(
                        @CurrentUser UserCredentials curr,
                        @PathVariable UUID targetId) {

                service.deleteFollowRequests(curr.getId(), targetId);
                return ResponseEntity.ok().body(
                                new SuccessResponse<Object>("successfully cancel the follow requests", null));
        }

        @GetMapping("/follow")
        public ResponseEntity<SuccessResponse<List<FollowerResponse>>> getFollowerFromusers(
                        @PathVariable UUID targetId,
                        HttpServletRequest request) {
                return ResponseEntity.ok().body(
                                new SuccessResponse<>("successfully getting data", service.getFollowerFromUsers(
                                                targetId, GetOptionalCurrentUsers.getViewerId(request))));
        }

}
