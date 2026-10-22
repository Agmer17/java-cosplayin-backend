package cosplayin.app.notification.controller;

import cosplayin.app.core.response.SuccessResponse;
import cosplayin.app.notification.model.dto.NotificationResponse;
import cosplayin.app.notification.service.NotificationService;
import cosplayin.app.security.anot.CurrentUser;
import cosplayin.app.security.anot.RequireAuth;
import cosplayin.app.security.context.UserCredentials;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Controller
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @RequireAuth
    public ResponseEntity<SuccessResponse<List<NotificationResponse>>> handleGetMyNotification(
            @CurrentUser UserCredentials curr) {
     return ResponseEntity.ok().body(
             new SuccessResponse<>("successfully getting your notifications", notificationService.getNotificationFromUsers(curr.getId()))
     );
    }
}
